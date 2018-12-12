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
	public String fichierMotsComp;
	public String dossierSortie;
	public boolean avecMotsComp;
	public boolean verbose;
	public boolean verbose2;
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
		fichierMotsComp = args[2];
		dossierSortie = args[3];
		avecMotsComp = args[7].equals("1") ? true : false;
		verbose = args[4].equals("1") ? true : false;
		verbose2 = args[5].equals("1") ? true : false;
		export_stats = args[6].equals("1") ? true : false;

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

		if (avecMotsComp)
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
		if(!verbose2)
			System.out.print("Recherche des relations --------------> ");

		/*
		 * Pour chaque template en partant du plus grand, on va parser le texte avec une
		 * fenetre de mots équivalente a la taille du template
		 */

		int taille_fenetre, col, ligne, nombre_de_lignes, begin = 0;
		int id_colonne = template_matrix.get_size();

		String tmpString = "";
		String previous = "";
		String following = "";

		/*
		 * On parcourt chaque colonne qui représente une taille de template à commencer
		 * par la plus grande
		 */
		for (col = 0; col < id_colonne; col++) {

			taille_fenetre = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à
			nombre_de_lignes = template_matrix.get_column_size(col); // la taille du template utilisé

			/*
			 * Tant que la taille du template n'est pas supérieure au nombre de mots
			 * restants dans le texte
			 */
			while (begin <= words.size() - taille_fenetre) {

				tmpString = " ";
				previous = " ";
				following = " ";

				ArrayList<String> tmpPrevious = new ArrayList<>();

				/* On crée un string temporaire contenant la fenetre à analyser */
				for (int index = begin; index < begin + taille_fenetre; index++)
					tmpString = tmpString + words.get(index) + " ";

				/*
				 * On vérifie s'il y a un match entre la fenetre de string et un des templates
				 */
				for (ligne = 0; ligne < nombre_de_lignes; ligne++) {

					/* S'il y a un match, on analyse ce qui precede et ce qui suit le template */
					if (tmpUtils.t_match(template_matrix.get_template(col, ligne), tmpString)) {

						if (verbose2) {
							System.out.println("\nPattern trouvé : " + tmpString + " --> "
									+ template_matrix.get_template(col, ligne).get_relation());
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

						if (verbose2)
							System.out.println("\n\t\tEnsemble de mots analysés avant : " + previous);

						previous = analyseStringForName(previous,
								template_matrix.get_template(col, ligne).getContrainteAnte());

						if (verbose2)
							System.out.println("\t\t\tMot(s) gardé(s) avant : " + previous);

						/*********************************/

						/*
						 * On recupere les 10 mots qui suivent le string temporaire, on s'arrete si on
						 * rencontre un point
						 */
						int start = begin + taille_fenetre - 1;

						for (int kndex = 1; kndex <= 10; kndex++)
							if (start + kndex < words.size())
								if (words.get(start + kndex).contains("."))
									break;
								else
									following = following + words.get(start + kndex) + " ";

						if (verbose2)
							System.out.println("\n\t\tEnsemble de mots analysés apres : " + following);

						following = analyseStringForName(following,
								template_matrix.get_template(col, ligne).getContraintePost());

						if (verbose2)
							System.out.println("\t\t\tMot(s) gardé(s) apres : " + following);

						/*********************************/

						tmpUtils.protectTemplate(words, begin, taille_fenetre);

						if (previous.length() > 0 && following.length() > 0)
							discovered_rel.add(previous + " --- "
									+ template_matrix.get_template(col, ligne).get_relation() + " --- " + following);

						/*********************************/

						break;

					}
				}
				begin++;
			}
			begin = 0;
		}

		Instant endloc = Instant.now();

		if (verbose2)
			System.out.println("\nRecherche des relations : " + Duration.between(begloc, endloc).toMillis() + " ms");
		else
			System.out.println(Duration.between(begloc, endloc).toMillis() + " ms");

		if (export_stats)
			fichierStats
					.write("Recherche des relations --------------> " + Duration.between(begloc, endloc).toMillis() + " ms\n");
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

		if (avecMotsComp)
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
			ptd.nettoyerPonctuation(usedFile, dossierSortie);
			data_file = new File(dossierSortie + "/document_intermediaire_pretraite.txt");
		} else
			data_file = new File(usedFile);

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			String[] tmpWords = line.split("[ ]+");

			for (String string : tmpWords)
				words.add(string);
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
			BufferedReader br = new BufferedReader(new FileReader(fichierMotsComp));
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
