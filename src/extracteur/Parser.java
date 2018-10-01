package extracteur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Parser {
	public static ArrayList<String> discovered_rel; // La liste des relations extraitent
	public static Template_matrix template_matrix; // La matrice de template
		
	public static boolean t_match(Template t, String word) {
		return false;
	}

	public static void main(String[] args) throws IOException {
		discovered_rel = new ArrayList<>();
		template_matrix = new Template_matrix("./data/templates");
//		template_matrix.show_template_matrix();

		File data_file = new File("./data/wiki_sample.txt");
		Scanner input = new Scanner(data_file);
		Scanner tmp_input = new Scanner(data_file);
		input.useDelimiter(" +"); //delimitor is one or more spaces
		tmp_input.useDelimiter(" +");
		/* Pour chaque template en partant du plus grand, on va parser le texte 
		 * avec une fenetre de mots équivalente a la taille du template
		 */
		String window_string;
		int window_size;
		int ws;
		int col;
		int line;
		int number_of_line;
		int number_of_col = template_matrix.get_size();
		/* Pour chaque collones de la matrice */
		for(col = 0; col < number_of_col; col++){
			window_size = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à la taille du template utilisé
//			System.out.println(window_size);
			number_of_line = template_matrix.get_column_size(col);
//			System.out.println(number_of_line);
			/* Pour chaque lignes de la matrice */
			for(line = 0; line < number_of_line; line++){
				/* On itère chaque mots du corpus */
				while(input.hasNext()){
					window_string = ""; // reset la window string
					tmp_input = input; // reset l'input temporaire
					/* Cette boucle construit la window_string */
					for(ws = 0; ws < window_size; ws++) {
						if(tmp_input.hasNext()) {
							window_string +=  " " + tmp_input.next(); // concatene la window string
						}
						else {
							break;
						}
//						window_string += tmp_input.next(); // concatene la window string
					}
					System.out.println(window_string);


				}
			}
		}
		
//		File template_file = new File("./data/templates");
//		load_templates();

		
//		for(Template temp : templates) System.out.println(temp.getSujet() + " --> " + temp.getObjet());
		

//		File file_p = new File("./data/wiki_sample.txt");
//		Scanner input=new Scanner(file_p);
//		input.useDelimiter(" +"); //delimitor is one or more spaces
//		String considered_word;
//		while(input.hasNext()){
//			considered_word = input.next();
//			for(Template temp : templates) {
//				if(t_match(temp, considered_word)){
//					discovered_rel.add("win");
//				}
//			}
//		}
//		
//		System.out.println("Job is done.");
//		System.out.println(discovered_rel);
		
	}

}
