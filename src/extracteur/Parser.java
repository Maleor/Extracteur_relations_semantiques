package extracteur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	public static ArrayList<Template> templates;

	public static void main(String[] args) throws IOException {

		templates = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader("./data/templates"));
		String line;

		while ((line = br.readLine()) != null) {
			String[] ligneTableau = line.split(";");
			for(int index = 1 ; index < ligneTableau.length-1 ; index++) {
				templates.add(new Template(ligneTableau[index], ligneTableau[0]));
			}
			
		}
		br.close();
		
		for(Template temp : templates) System.out.println(temp.getSujet() + " --> " + temp.getObjet());

	}

}
