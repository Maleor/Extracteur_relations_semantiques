package pretraitement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PretraitementDocument {
	
	public PretraitementDocument() {
		
	}
	
	public void espacerPonctuation(String file) throws IOException {
		
		FileWriter fwloc = new FileWriter("data/doc_pretraite.txt");
		
		File data_file = new File(file);

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			String[] tmpWords = line.split("[,;=.]+");

			for (String string : tmpWords)
				fwloc.write(string);
		}

		fwloc.close();
		scanner.close();
	}

}
