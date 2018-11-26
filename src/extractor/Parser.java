package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.collections4.trie.PatriciaTrie;

import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;
import requeterRezo.Filtre;
import template.Template;
import template.Template_matrix;

public class Parser {

	private ArrayList<String> discovered_rel; // La liste des relations extraites
	
	private Template_matrix template_matrix; // La matrice de template

	private RequeterRezoDump systeme;

	private ArrayList<String> words;

	private PatriciaTrie<Integer> mots_composes;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////
	public Parser() throws IOException {
		systeme = new RequeterRezoDump("12h", "100mo");

		discovered_rel = new ArrayList<>();
		template_matrix = new Template_matrix("./data/templates");

		words = new ArrayList<>();

		mots_composes = new PatriciaTrie<>();
		initLists();
	}

	//////////////////////
	/** PUBLIC METHODS **/
	//////////////////////
	public void run() throws IOException {

		initWords();

		/*
		 * Pour chaque template en partant du plus grand, on va parser le texte avec une
		 * fenetre de mots équivalente a la taille du template
		 */

		int window_size, col, line, number_of_line, begin = 0;
		int number_of_col = template_matrix.get_size();
		String tmpString = "";
		String previous = "";
		String following = "";
		FileWriter fw = new FileWriter("data/extracted.txt");

		/* Pour chaque colonnes de la matrice */
		for (col = 0; col < number_of_col; col++) {

			window_size = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à la
																			// taille du template utilisé
			number_of_line = template_matrix.get_column_size(col);

			while (begin <= words.size() - window_size) {

				tmpString = " ";
				previous = " ";
				following = " ";

				/* On crée un string temporaire contenant la fenetre à analyser */
				for (int index = begin; index < begin + window_size; index++)
					tmpString = tmpString + words.get(index) + " ";

				/*
				 * On vérifie s'il y a un match entre la fenetre de string et un des templates
				 */
				for (line = 0; line < number_of_line; line++) {
					if (t_match(template_matrix.get_template(col, line), tmpString)) {

						/*********************************/

						/* On recupere les 10 mots qui precedent le string temporaire */
						for (int jndex = 10; jndex > 0; jndex--)
							if (begin - jndex >= 0)
								previous = previous + words.get(begin - jndex) + " ";
						previous = analyseStringForName(previous);

						/*********************************/

						/* On recupere les 10 mots qui suivent le string temporaire */
						int start = begin + window_size - 1;
						for (int kndex = 1; kndex <= 10; kndex++)
							if (start + kndex < words.size())
								following = following + words.get(start + kndex) + " ";
						// following = analyseStringForName(following);

						/*********************************/

						/* On ecrit en sortie le template avec ce qui precede et ce qui suit */
						discovered_rel.add(previous + " --- " + template_matrix.get_template(col, line).get_relation() + " --- "
								+ following);

						break;
					}
				}
				begin++;
			}
			begin = 0;
		}
		fw.close();

	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////

	/**
	 * Vérifie si un chaine de caractère existe comme template
	 * 
	 * @param t
	 *            Le template avec lequel on compare la chaine de caractères
	 * 
	 * @param word
	 *            La chaine de caractère que l'on compare
	 * 
	 * @return True si la chaine de caractères est un template, false sinon.
	 */
	private boolean t_match(Template t, String word) {
		return t.get_eln().equals(word);
	}

	/**
	 * Crée un Mot à partir d'un string et vérifie si ca peut etre un nom
	 * 
	 * @param word
	 *            Le string à partir duquel on crée un Mot
	 * 
	 * @return True si word peut etre un nom, false sinon
	 */
	private boolean analyseWord(String word) {
		if (word.equals(""))
			return false;

		boolean toRet;

		Mot m = systeme.requete(word, 4, Filtre.FiltreRelationsEntrantes);

		toRet = (m != null) ? estNom(m) : false;

		return toRet;

	}

	/**
	 * Analyse un chaine de caractères pour vérifier si elle peut être utilisée
	 * comme un nom ou un nom composé
	 * 
	 * @param str
	 *            La chaine de caractères que l'on analyse
	 * 
	 * @return La chaine de caractères finale qui accompagnera la template
	 */
	private String analyseStringForName(String str) {
		String finalName = "";
		String[] wd = str.split(" ");

		for (String s : wd) {
			if (analyseWord(s)) {
				finalName = s;
				break;
			}
		}

		return finalName;
	}

	/**
	 * Initialise la liste des mots qui sera utilisée pour créer des fenêtres de
	 * String
	 * 
	 * @throws FileNotFoundException
	 */
	private void initWords() throws FileNotFoundException {
		File data_file = new File("./data/wiki_sample.txt");

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			String[] tmpWords = line.split(" ");

			for (String string : tmpWords)
				words.add(string);
		}

		scanner.close();
	}

	/**
	 * Analyse un Mot est vérifie s'il peut etre un nom
	 * 
	 * @param cible
	 *            Le Mot que l'on analyse
	 * 
	 * @return True si le Mot peut etre un nom, false sinon.
	 */
	private boolean estNom(Mot cible) {

		ArrayList<Voisin> voisins = new ArrayList<>();
		voisins = cible.getRelations_sortantes(4);

		if (voisins != null) {
			for (Voisin v : voisins) {
				if (v.getNom().startsWith("Nom")) {
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean estNomCompose(String str) {
		boolean toRet = false;
		
		
		
		return toRet;
	}

	private void initLists() {

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
	}
}
