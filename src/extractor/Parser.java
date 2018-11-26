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
	private ArrayList<String> words;

	private Template_matrix template_matrix; // La matrice de template

	private RequeterRezoDump systeme; // Instance du requeteur sur Jeux de Mots
	
	private PatriciaTrie<Integer> mots_composes;
	
	private FileWriter fw;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////
	public Parser() throws IOException {
		systeme = new RequeterRezoDump("12h", "100mo");

		discovered_rel = new ArrayList<>();
		template_matrix = new Template_matrix("./data/templates");
		fw = new FileWriter("data/extracted.txt");
		words = new ArrayList<>();

		mots_composes = new PatriciaTrie<>();
		initMotComposes();
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

				/* On crée un string temporaire contenant la fenetre à analyser */
				for (int index = begin; index < begin + window_size; index++)
					tmpString = tmpString + words.get(index) + " ";

				/*
				 * On vérifie s'il y a un match entre la fenetre de string et un des templates
				 */
				for (line = 0; line < number_of_line; line++) {

					if (t_match(template_matrix.get_template(col, line), tmpString)) {

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
						following = analyseStringForName(following);

						/*********************************/

						/*
						 * Si un template est trouvé, on fait en sorte que les mots à l'interieur ne
						 * soient pas utilisés pour d'autres templates plus petits en collant chaque mot
						 * avec "_"
						 * 
						 * Exemple : Si le template "est un" est trouvé, on supprime le mot "un" et on
						 * remplace "est" par "est_un".
						 */

						String protectTemplate = "";

						for (int index = begin; index < begin + window_size; index++)
							protectTemplate = protectTemplate + "_" + words.get(index);

						for (int index = begin + 1; index < begin + window_size; index++)
							words.remove(index);

						words.set(begin, protectTemplate.substring(1, protectTemplate.length()));

						/* On ecrit en sortie le template avec ce qui precede et ce qui suit */
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
	 * @return Le poids de la relation si le Mot peut etre un nom, -1 sinon.
	 */
	private int analyseWord(String word) {
		if (word.equals(""))
			return -1;

		int toRet;

		Mot m = systeme.requete(word, 4, Filtre.FiltreRelationsEntrantes);

		toRet = (m != null) ? estNom(m) : -1;

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

		finalName = nomCompose(str);

		int maxPoids = 0;
		int tmpValue;

		if (finalName.equals("")) {

			String[] wd = str.split(" ");

			for (String s : wd) {
				tmpValue = analyseWord(s);
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
	 * @throws FileNotFoundException
	 */
	private void initWords() throws FileNotFoundException {
		File data_file = new File("./data/manual_file");

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			String[] tmpWords = line.split("[ .]+");

			for (String string : tmpWords)
				if(!string.equals(" "))
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
	 * @return Le poids de la relation si le Mot peut etre un nom, -1 sinon.
	 */
	private int estNom(Mot cible) {

		ArrayList<Voisin> voisins = new ArrayList<>();
		voisins = cible.getRelations_sortantes(4);

		if (voisins != null) {
			for (Voisin v : voisins) {
				if (v.getNom().startsWith("Nom")) {
					return v.getPoids();
				}
			}
		}

		return -1;
	}

	/**
	 * Vérifie si un mot composé est présent dans une chaine de caractères
	 * 
	 * @param str
	 *            Chaine de caracteres analysée
	 * 
	 * @return Le mot composé s'il y en a un, chaine de caractères vide sinon
	 */
	private String nomCompose(String str) {

		String toRet = "";

		String[] wd = str.split(" ");
		String tmpString = "";

		ArrayList<String> mots = new ArrayList<>();

		for (String s : wd) {
			if (!s.equals(""))
				mots.add(s);
		}

		for (int index = mots.size(); index > 0; index--) {

			tmpString = "";

			for (int jndex = mots.size() - index; jndex < mots.size(); jndex++) {
				tmpString = tmpString + " " + mots.get(jndex);
			}

			tmpString = tmpString.substring(1, tmpString.length());

			if (mots_composes.containsKey(tmpString)) {
				toRet = tmpString;
				break;
			}
		}
		return toRet;
	}

	/**
	 * Initialise la liste de mots composés à partir d'un fichier donné
	 */
	private void initMotComposes() {

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

	/**
	 * Exports les résultats dans le fichier de sortie
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
