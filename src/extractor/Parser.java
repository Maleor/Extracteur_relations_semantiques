package extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import RequeterRezo.Mot;
import RequeterRezo.RequeterRezo;
import RequeterRezo.Terme;
import template.Template;
import template.Template_matrix;

public class Parser {

	public ArrayList<String> discovered_rel; // La liste des relations extraitent
	public Template_matrix template_matrix; // La matrice de template

	public RequeterRezo systeme;

	public ArrayList<String> words;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////
	public Parser() throws IOException {
		systeme = new RequeterRezo("12h", 10000);

		discovered_rel = new ArrayList<>();
		template_matrix = new Template_matrix("./data/templates");

		words = new ArrayList<>();
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
		FileWriter fw = new FileWriter("data/extracted.txt");

		/* Pour chaque colonnes de la matrice */
		for (col = 0; col < number_of_col; col++) {

			window_size = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à la
																			// taille du template utilisé
			number_of_line = template_matrix.get_column_size(col);

			while (begin <= words.size() - window_size) {

				tmpString = " ";

				for (int index = begin; index < begin + window_size; index++) {
					tmpString = tmpString + words.get(index)
							+ " "; /* on créé un string temporaire contenant la fenetre à analyser */
				}
				begin++;

				/*
				 * On vérifie s'il y a un match entre la fenetre de string et un des templates
				 */
				
				
				analyseStringForName(tmpString);
				
				for (line = 0; line < number_of_line; line++) {
					if (t_match(template_matrix.get_template(col, line), tmpString)) {
						fw.write(template_matrix.get_template(col, line).get_template_under_string_shape() + "\n");
						break;
					}
				}

			}
			begin = 0;

		}
		fw.close();

	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////
	private Mot requestWord(String word) {
		try {
			return systeme.requete(word, true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("failed");
		return null;
	}

	private boolean t_match(Template t, String word) {
		return t.get_eln().equals(word);
	}

	
	
	private String analyseWord(String word) {
		if(word.equals(""))
				return "";
		
		System.out.println("Le mot est : " + word);		
		Mot m = requestWord(word);
		return getClaGrammFromWord(m, 0);
	}

	private void analyseStringForName(String str) {
		String[] wd = str.split(" ");
		
		String cg;
		for(String s : wd) {
			cg = analyseWord(s);
			System.out.println(cg);
		}
	}
	
	
	
	private void initWords() throws FileNotFoundException {
		File data_file = new File("./data/wiki_sample.txt");

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine(); // lecture de la ligne
			String[] tmpWords = line.split(" "); // recuperation des mots de la ligne
			for (String string : tmpWords)
				words.add(string);
		}

		scanner.close();
	}

	/**
	 * Recuperer les classes grammaticales d'un mot
	 * 
	 * @param m
	 *            Le mot dont on recupere les classes grammaticales
	 * @param index
	 *            Rang dans la liste des cla. gram. pour savoir laquelle on retoune.
	 *            
	 * @return La classe grammaticale du mot m au rang index du mot / null sinon
	 */
	private String getClaGrammFromWord(Mot m, int index) {

		HashMap<String, ArrayList<Terme>> req = m.getRelations_sortantes();
		ArrayList<Terme> termes = req.get("r_pos");

		if (termes != null)
			return termes.get(index).getTerme();
		else
			return null;
	}

}
