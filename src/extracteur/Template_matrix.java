package extracteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Template_matrix {
	
	private static Vector<Vector<Template>> templates_matrix;
	
	/* Construit la matrice de templates à partir du fichier */
	public Template_matrix(String path_to_template_file) throws IOException {
		Vector<Template> tmp_templates = new Vector<Template>(); // Un vecteur de template temporaire
		templates_matrix = new Vector<Vector<Template>>(); // Construit la matrice de template 
		
		BufferedReader br = new BufferedReader(new FileReader("./data/templates"));
		// Cette premiere boucle charge tous les template dans le vecteur temporaire
		String line;
		while ((line = br.readLine()) != null) {
			String[] ligneTableau = line.split(";");
			Template t = new Template(ligneTableau[0], ligneTableau[1]);
			tmp_templates.add(t);
		}	
		br.close();
			
		// Cette seconde boucle trouve la longueur maximum des templates
		int i;
		int max = 0;
		for(i=0; i < tmp_templates.size(); i++) {
			if(max < tmp_templates.get(i).get_t_length()) {
				max = tmp_templates.get(i).get_t_length();
			}
		}
		
		// Cette troisieme boucle va remplir la matrice de templates en partant de 'lindice le plus grand : max
		int template_length;
		int template_indice;
		for(template_length = max; template_length > 0; template_length--) { // pour chaque valeur de longueur en partant de la plus grande
			Vector<Template> col = new Vector<>();
			for(template_indice = 0; template_indice < tmp_templates.size(); template_indice++ ) { // pour chaque template dans la liste temporaire
				if(tmp_templates.get(template_indice).get_t_length() == template_length) { // si la taille du template correspond à l'indice de notre boucle
					col.add(tmp_templates.get(template_indice)); // on ajoute le template dans la collone
				}
			}
			templates_matrix.add(col); // On ajoute la collone à la matrice
		}

	}
	
	public int get_template_size_at_column(int c){
		return templates_matrix.get(c).get(0).get_t_length();
	}
	
	public int get_size(){
		return this.templates_matrix.size();
	}
	
	public int get_column_size(int c){
		return this.templates_matrix.get(c).size();
	}
	
	/* Affiche la matrice de template */ 
	public void show_template_matrix() {
		int i;
		int j;
		int number_of_line;
		int number_of_col = templates_matrix.size();
		for(i = 0; i < number_of_col; i++) {
			number_of_line = templates_matrix.get(i).size();
			for(j = 0; j < number_of_line; j++) {
				System.out.println(templates_matrix.get(i).get(j).get_template_under_string_shape());
			}
		}
	}
	




}
