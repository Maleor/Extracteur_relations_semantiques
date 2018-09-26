package extracteur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	
	public static ArrayList<String> templates;

	public static void main(String[] args) throws IOException {
		
		templates = new ArrayList<>();
		
		BufferedReader br = new BufferedReader(new FileReader("./data/templates"));
		String line;
		while ((line = br.readLine()) != null) {
		   System.out.println(line);
		}
		br.close();

	}

}
