package extracteur;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	public static ArrayList<String> discovered_rel; // La liste des relations extraitent
	public static Template_matrix template_matrix; // La matrice de template
		
	public static boolean t_match(Template t, String word) {
		return t.get_eln().equals(word);
	}

	public static void main(String[] args) throws IOException {
		discovered_rel = new ArrayList<>();
		template_matrix = new Template_matrix("./data/templates");
		//template_matrix.show_template_matrix();

		File data_file = new File("./data/wiki_sample.txt");
		Scanner input = new Scanner(data_file);
		Scanner tmp_input = new Scanner(data_file);
		input.useDelimiter(" +"); //delimitor is one or more spaces
		tmp_input.useDelimiter(" +");
		
		
		
		Scanner scanner=new Scanner(data_file);
		ArrayList<String> words = new ArrayList<>();
		 
		while (scanner.hasNextLine()) {
 
		    String line = scanner.nextLine(); //lecture de la ligne
		    String[] tmpWords = line.split(" "); //recuperation des mots de la ligne
		    for(String string : tmpWords) words.add(string);
		}
 
		scanner.close();
		
		//for(String string : words) System.out.println(string);
		
		
		
		/* Pour chaque template en partant du plus grand, on va parser le texte 
		 * avec une fenetre de mots équivalente a la taille du template
		 */

		int window_size, col, line, number_of_line, begin = 0;
		int number_of_col = template_matrix.get_size();
		String tmpString = "";
		FileWriter fw = new FileWriter("data/extracted.txt");
		
		
		/* Pour chaque colonnes de la matrice */
		for(col = 0; col < number_of_col; col++){
			
			window_size = template_matrix.get_template_size_at_column(col); // La taille d'une fenetre correspond à la taille du template utilisé
			number_of_line = template_matrix.get_column_size(col);
			
			while(begin <= words.size() - window_size){
				
				tmpString = " ";
				for(int index = begin ; index < begin + window_size ; index++) {
					tmpString = tmpString + words.get(index) + " "; /* on créé un string temporaire contenant la fenetre à analyser */
				}
				begin++;
				
			//	System.out.println(tmpString);
				
				
				/* On vérifie s'il y a un match entre la fenetre de string et un des templates */
				for(line = 0; line < number_of_line; line++){
					if(t_match(template_matrix.get_template(col, line), tmpString)) {
						fw.write(template_matrix.get_template(col, line).get_template_under_string_shape() + "\n");
					}
								
				}
			}
			begin = 0;	
			
		}
		fw.close();
		

		
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
