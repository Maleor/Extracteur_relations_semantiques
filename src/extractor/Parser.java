package extractor;

import java.io.BufferedReader;
import java.io.File;
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
import requeterRezo.RequeterRezoDump;
import template.TemplateUtils;
import template.Template_matrix;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Parser {

	public String fichierCible;
	public String fichierRegles;
	public String dossierSortie;
	public boolean verbose;
	public boolean export_stats;

	private ArrayList<String> discovered_rel; // La liste des relations extraites
	private ArrayList<String> words;

	private Template_matrix template_matrix; // La matrice de templates

	private RequeterRezoDump systeme; // Instance du requeteur sur Jeux de Mots

	private PatriciaTrie<Integer> mots_composes; // Ensemble des mots composés

	private FileWriter fichierResultats; // Fichier de sortie pour les résultats
	private FileWriter fichierStats; // Fichier de sortie pour les stats

	private AnalyseurMotsComposes analyMC;
	private AnalyseurMotSeul analyMS;
	private TemplateUtils tmpUtils;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////
	public Parser(String[] args) throws IOException {

		fichierRegles = args[0];
		fichierCible = args[1];
		dossierSortie = args[2];
		verbose = args[3].equals("1") ? true : false;
		export_stats = args[4].equals("1") ? true : false;

		systeme = new RequeterRezoDump("12h", "100mo");

		discovered_rel = new ArrayList<>();

		template_matrix = new Template_matrix(fichierRegles);

		fichierResultats = new FileWriter(dossierSortie + "/relations_extraites.txt");

		if (export_stats)
			fichierStats = new FileWriter(dossierSortie + "/statistiques_execution.txt");

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

		initWords(true);

		recherchePattern();

		export();

	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////

	private void recherchePattern() throws IOException {

		Instant begloc = Instant.now();

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

						if (verbose) {
							System.out.println("\nPattern trouvé : " + tmpString + " --> "
									+ template_matrix.get_template(col, line).get_relation());
							System.out.println("\tAnalyse de l'entourage du pattern en cours...");
						}

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

						if (verbose)
							System.out.println("\n\t\tEnsemble de mots analysés avant : " + previous);

						previous = analyseStringForName(previous,
								template_matrix.get_template(col, line).getContrainteAnte());

						if (verbose)
							System.out.println("\t\t\tMot(s) gardé(s) avant : " + previous);

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

						if (verbose)
							System.out.println("\n\t\tEnsemble de mots analysés apres : " + following);

						following = analyseStringForName(following,
								template_matrix.get_template(col, line).getContraintePost());

						if (verbose)
							System.out.println("\t\t\tMot(s) gardé(s) apres : " + following);

						/*********************************/

						tmpUtils.protectTemplate(words, begin, window_size);

						if (previous.length() > 0 && following.length() > 0)
							discovered_rel.add(previous + " --- "
									+ template_matrix.get_template(col, line).get_relation() + " --- " + following);

						/*********************************/

						break;

					}
				}
				begin++;
			}
			begin = 0;
		}

		Instant endloc = Instant.now();

		System.out.println("\nRecherche des relations : " + Duration.between(begloc, endloc).toMillis() + " ms");

		if (export_stats)
			fichierResultats
					.write("\nRecherche des relations : " + Duration.between(begloc, endloc).toMillis() + " ms\n");
	}

	/**
	 * Analyse une chaine de caractères pour vérifier si elle peut être utilisée
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

				tmpValue = analyMS.analyseWord(s, contrainte, systeme);

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

		String usedFile = fichierCible;
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
				words.add(string);
//				if (!string.equals(" ") && !string.equals("")) {
//
//					/*
//					 * Les ',' ';' '.' sont tous remplacés par des ' . ', ce qui permet d'isoler la
//					 * ponctuation des mots. De plus nous considérons que virgules et
//					 * points-virgules ont le meme comportement que le point.
//					 */
//					if (string.contains(".") || string.contains(",") || string.contains(";")) {
//
//						String newStr = string.replaceAll("[;,.]+", " . ");
//
//						String[] tmp = newStr.split("[ ]+");
//
//						for (String str : tmp)
//							words.add(str);
//					} else if (!string.contains("="))
//						words.add(string);
//				}
			}
		}

		scanner.close();

		Instant endloc = Instant.now();

		System.out.println(Duration.between(begloc, endloc).toMillis() + " ms");

		if (export_stats)
			fichierStats.write(
					"Initialisation des mots à analyser ---> " + Duration.between(begloc, endloc).toMillis() + " ms\n");
	}

	/**
	 * Initialise la liste de mots composés à partir d'un fichier donné
	 * 
	 * @throws IOException
	 */
	private void initMotComposes() throws IOException {

		System.out.print("Initialisation des mots composés -----> ");
		Instant begloc = Instant.now();
		Instant endloc;

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
		endloc = Instant.now();

		System.out.println(Duration.between(begloc, endloc).toMillis() + " ms");

		if (export_stats)
			fichierStats.write(
					"Initialisation des mots composés -----> " + Duration.between(begloc, endloc).toMillis() + " ms\n");
	}

	/**
	 * Exporte les résultats dans le fichier de sortie
	 */
	private void export() {

		try {
			for (String str : discovered_rel) {
				fichierResultats.write(str + "\n");
			}
			fichierResultats.close();

			if (export_stats)
				fichierStats.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
