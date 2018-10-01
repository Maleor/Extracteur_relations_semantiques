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
		template_matrix.show_template_matrix();

		File data_file = new File("./data/wiki_sample.txt");
		Scanner input = new Scanner(data_file);
		input.useDelimiter(" +"); //delimitor is one or more spaces
		
		/* Pour chaue template en partant du plus grand, on va parser le texte 
		 * avec une fenetre de mots équivalente a la taille du template
		 */
		int col;
		int line;
		int number_of_line;
		int number_of_col = template_matrix.get_size();
		for(col = 0; col < number_of_col; col++){
			number_of_line = template_matrix.get_column_size(col);
			for(line = 0; line < number_of_line; line++){
				
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
