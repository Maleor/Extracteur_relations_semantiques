package template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Template_matrix {

	private Vector<Vector<Template>> templates_matrix;

	private int max_TemplateSize;

	///////////////////
	/** CONSTRUCTOR **/
	///////////////////

	/**
	 * Construit la matrice de templates à partir du fichier
	 * 
	 * @param path_to_template_file
	 *            Le fichier qui contient les différents templates
	 * 
	 * @throws IOException
	 */
	public Template_matrix(String path_to_template_file) throws IOException {

		Vector<Template> tmp_templates = new Vector<Template>(); // Un vecteur de template temporaire

		templates_matrix = new Vector<Vector<Template>>(); // Construit la matrice de template

		tmp_templates = get_All_Templates(path_to_template_file); // recuperer les templates

		max_TemplateSize = find_Max_TemplateSize(tmp_templates); // trouver la taille max de template

		sort_Templates(tmp_templates); // trier les templates par taille dans la liste finale

	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////

	/**
	 * Récupére tous les templates à partir d'un fichier sans se soucier de leur
	 * taille
	 * 
	 * @param tmp_templates
	 *            Le fichier qui contient les templates
	 *            
	 * @return Le vecteur qui contient les templates
	 * 
	 * @throws IOException
	 */
	private Vector<Template> get_All_Templates(String path_to_template_file) throws IOException {

		Vector<Template> templates = new Vector<>();
		
		BufferedReader br = new BufferedReader(new FileReader(path_to_template_file));

		String line;

		while ((line = br.readLine()) != null) {

			Template t;

			String[] ligneTableau = line.split(";");

			if (ligneTableau.length != 4)
				t = new Template(ligneTableau[0], ligneTableau[1], "", "");
			else
				t = new Template(ligneTableau[1], ligneTableau[2], ligneTableau[0], ligneTableau[3]);

			templates.add(t);
		}
		br.close();
		
		return templates;
	}

	/**
	 * Cherche la taille de template maximale
	 * 
	 * @param tmp_templates
	 *            La liste des template
	 *            
	 * @return La taille max
	 */
	private int find_Max_TemplateSize(Vector<Template> tmp_templates) {

		int max = 0;
		
		for (int index = 0; index < tmp_templates.size(); index++) 
			if (max < tmp_templates.get(index).get_t_length()) 
				max = tmp_templates.get(index).get_t_length();
		
		return max;
	}

	/**
	 * Crée la liste finale de templates en les triant par taille
	 * 
	 * @param tmp_templates
	 *            La liste temporaire de templates
	 */
	private void sort_Templates(Vector<Template> tmp_templates) {
		
		for (int template_length = max_TemplateSize; template_length > 0; template_length--) {

			Vector<Template> col = new Vector<>();

			for (int template_indice = 0; template_indice < tmp_templates.size(); template_indice++)
				if (tmp_templates.get(template_indice).get_t_length() == template_length)
					col.add(tmp_templates.get(template_indice));

			templates_matrix.add(col);
		}
	}

	//////////////////////
	/** PUBLIC METHODS **/
	//////////////////////

	public int get_template_size_at_column(int c) {
		return templates_matrix.get(c).get(0).get_t_length();
	}

	public int get_size() {
		return templates_matrix.size();
	}

	public int get_column_size(int c) {
		return templates_matrix.get(c).size();
	}

	public Template get_template(int i, int j) {
		return templates_matrix.get(i).get(j);
	}

	/* Affiche la matrice de template */
	public void show_templates_matrix() {

		int number_of_line;
		int number_of_col = templates_matrix.size();
		
		for (int i = 0; i < number_of_col; i++) {
			
			number_of_line = templates_matrix.get(i).size();
			
			for (int j = 0; j < number_of_line; j++) 
				System.out.println(templates_matrix.get(i).get(j).get_template_under_string_shape());		
		}
	}

}
