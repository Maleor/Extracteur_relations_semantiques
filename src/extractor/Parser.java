package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.apache.commons.collections4.trie.PatriciaTrie;

import analyserMots.AnalyseurMotSeul;
import analyserMots.AnalyseurMotsComposes;
import pretraitement.PretraitementDocument;
import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;
import requeterRezo.Filtre;
import template.Template;
import template.TemplateUtils;
import template.Template_matrix;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Parser {

	private ArrayList<String> discovered_rel; // La liste des relations extraites
	private ArrayList<String> words;

	private Template_matrix template_matrix; // La matrice de template

	private RequeterRezoDump systeme; // Instance du requeteur sur Jeux de Mots

	private PatriciaTrie<Integer> mots_composes;

	private FileWriter fw;

	private AnalyseurMotsComposes analyMC;
	private AnalyseurMotSeul analyMS;
	private TemplateUtils tmpUtils;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////
	public Parser() throws IOException {
		systeme = new RequeterRezoDump("12h", "100mo");

		discovered_rel = new ArrayList<>();

		template_matrix = new Template_matrix("./data/templates");

		fw = new FileWriter("data/extracted.txt");

		words = new ArrayList<>();

		analyMS = new AnalyseurMotSeul();
		tmpUtils = new TemplateUtils();

		mots_composes = new PatriciaTrie<>();

	}

	//////////////////////
	/** PUBLIC METHODS **/
	//////////////////////
	public void run() throws IOException {

		initMotComposes();
		initWords(false);

		/*
		 * Pour chaque template en partant du plus grand, on va parser le texte avec une
		 * fenetre de mots équivalente a la taille du template
		 */

		int window_size, col, line, number_of_line, begin = 0;
		int number_of_col = template_matrix.get_size();

		String tmpString = "";
		String previous = "";
		String following = "";

		/*
		 * On parcourt chaque colonne qui représente une taille de template à commencer
		 * par la plus grande
		 */
		for (col = 0; col < number_of_col; col++) {

			window_size = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à la
																			// taille du template utilisé
			number_of_line = template_matrix.get_column_size(col);

			/*
			 * Tant que la taille du template n'est pas supérieure au nombre de mots
			 * restants dans le texte
			 */
			while (begin <= words.size() - window_size) {

				tmpString = " ";
				previous = " ";
				following = " ";

				ArrayList<String> tmpPrevious = new ArrayList<>();

				/* On crée un string temporaire contenant la fenetre à analyser */
				for (int index = begin; index < begin + window_size; index++)
					tmpString = tmpString + words.get(index) + " ";

				/*
				 * On vérifie s'il y a un match entre la fenetre de string et un des templates
				 */
				for (line = 0; line < number_of_line; line++) {

					/* S'il y a un match, on analyse ce qui precede et ce qui suit le template */
					if (tmpUtils.t_match(template_matrix.get_template(col, line), tmpString)) {

						/*
						 * On recupere les 10 mots qui precedent le string temporaire, on s'arrete si on
						 * rencontre un point
						 */
						for (int jndex = 1; jndex < 10; jndex++)
							if (begin - jndex >= 0)
								if (words.get(begin - jndex).contains("."))
									break;
								else
									tmpPrevious.add(words.get(begin - jndex));

						Collections.reverse(tmpPrevious);

						for (String str : tmpPrevious)
							previous = previous + str + " ";

						previous = analyseStringForName(previous, template_matrix.get_template(col, line).getContrainteAnte());

						/*********************************/

						/*
						 * On recupere les 10 mots qui suivent le string temporaire, on s'arrete si on
						 * rencontre un point
						 */
						int start = begin + window_size - 1;

						for (int kndex = 1; kndex <= 10; kndex++)
							if (start + kndex < words.size())
								if (words.get(start + kndex).contains("."))
									break;
								else
									following = following + words.get(start + kndex) + " ";

						following = analyseStringForName(following, template_matrix.get_template(col, line).getContraintePost());

						/*********************************/

						tmpUtils.protectTemplate(words, begin, window_size);

						discovered_rel.add(previous + " --- " + template_matrix.get_template(col, line).get_relation()
								+ " --- " + following);

						/*********************************/

						break;

					}
				}
				begin++;
			}
			begin = 0;
		}

		export();

	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////

	/**
	 * Crée un Mot à partir d'un string et vérifie si ca peut etre un nom
	 * 
	 * @param word
	 *            Le string à partir duquel on crée un Mot
	 * 
	 * @return Le poids de la relation si le Mot peut etre un nom, -1 sinon.
	 */
	private int analyseWord(String word, String contrainte) {
		if (word.equals(""))
			return -1;

		int toRet;

		Mot m = systeme.requete(word, 4, Filtre.FiltreRelationsEntrantes);

		if(contrainte.equals("") || contrainte.equals(" "))
			contrainte = "Nom";
		
		switch(contrainte) {
		
		case " Adj " :
		case " adj " :
		case "Adj " :
		case "adj " :
		case "Adj" :
		case "adj" :
		case " Adj" :
		case " adj" :
			contrainte = "Adj";
			break;
			
		case "":
		case " ":
		default :
			contrainte = "Nom";
			break;
			
		}
		toRet = (m != null) ? analyMS.respecteContrainte(m, contrainte) : -1;

		return toRet;

	}

	/**
	 * Analyse un chaine de caractères pour vérifier si elle peut être utilisée
	 * comme un nom ou un nom composé
	 * 
	 * @param str
	 *            La chaine de caractères que l'on analyse
	 * 
	 * @return La chaine de caractères finale qui accompagnera le template
	 */
	private String analyseStringForName(String str, String contrainte) {

		String finalName = "";

		finalName = analyMC.nomCompose(str);

		int maxPoids = 0;
		int tmpValue;

		if (finalName.equals("")) {

			String[] wd = str.split(" ");

			for (String s : wd) {

				tmpValue = analyseWord(s, contrainte);

				if (tmpValue != -1) {

					if (tmpValue > maxPoids) {
						finalName = s;
						maxPoids = tmpValue;
					}
				}
			}
		}

		return finalName;
	}

	/**
	 * Initialise la liste des mots qui sera utilisée pour créer des fenêtres de
	 * String
	 * 
	 * @param avecPT
	 *            Booleen qui indique si oui ou non on souhaite faire un
	 *            pre-traitement sur le document.
	 * 
	 * @throws IOException
	 */
	private void initWords(boolean avecPT) throws IOException {

		System.out.print("Initialisation des mots à analyser ---> ");
		Instant begloc = Instant.now();

		String usedFile = "data/wiki_sample.txt";
		File data_file;

		if (avecPT) {
			PretraitementDocument ptd = new PretraitementDocument();
			ptd.espacerPonctuation(usedFile);
			data_file = new File("data/doc_pretraite.txt");
		} else
			data_file = new File(usedFile);

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			String[] tmpWords = line.split("[ ]+");

			for (String string : tmpWords) {
				if (!string.equals(" ") && !string.equals("")) {

					/*
					 * Les ',' ';' '.' sont tous remplacés par des ' . ', ce qui permet d'isoler la
					 * ponctuation des mots. De plus nous considérons que virgules et
					 * points-virgules ont le meme comportement que le point.
					 */
					if (string.contains(".") || string.contains(",") || string.contains(";")) {

						String newStr = string.replaceAll("[;,.]+", " . ");

						String[] tmp = newStr.split("[ ]+");

						for (String str : tmp)
							words.add(str);
					} else
						words.add(string);
				}
			}
		}

		scanner.close();

		System.out.println(Duration.between(begloc, Instant.now()).toMillis() + " ms");
	}

	/**
	 * Initialise la liste de mots composés à partir d'un fichier donné
	 */
	private void initMotComposes() {

		System.out.print("Initialisation des mots composés -----> ");
		Instant begloc = Instant.now();

		String line1;
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/mots_composes.txt"));
			while ((line1 = br.readLine()) != null) {
				mots_composes.put(line1, 0);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		analyMC = new AnalyseurMotsComposes(mots_composes);

		System.out.println(Duration.between(begloc, Instant.now()).toMillis() + " ms");
	}

	/**
	 * Exporte les résultats dans le fichier de sortie
	 */
	private void export() {

		try {
			for (String str : discovered_rel) {
				fw.write(str + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
