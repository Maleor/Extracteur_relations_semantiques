package pretraitement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Mathieu Dodard
 * @Author Jordan Guillonneau
 *
 */
public class PretraitementDocument {

	public PretraitementDocument() {

	}

	/**
	 * Pré-traite un document en remplaçant les virgules et points-virgules par des
	 * points, de plus tous les points sont entourés par des espaces pour éviter
	 * qu'ils soient collés à un mot. Le reste de la ponctuation est supprimé sauf
	 * les - et _
	 * 
	 * @param file
	 *            Le fichier à traiter
	 * 
	 * @throws IOException
	 */
	public void nettoyerPonctuation(String file, String dossierExport) throws IOException {

		FileWriter fwloc = new FileWriter(dossierExport + "/document_intermediaire_pretraite.txt");

		File data_file = new File(file);

		Scanner scanner = new Scanner(data_file);

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			if (!line.equals("") && !line.equals(" ")) {
				String[] tmpWords = line.split("[ ]+");

			
				String newStr;

				for (String string : tmpWords) {

					newStr = string.replaceAll("[;,.]+", " . ");
					newStr = newStr.replaceAll("[\\[\\]«»=\\(\\)\\{\\}'\"]+", " ");

					String[] tmp = newStr.split("[ ]+");

					for (String st : tmp)
						if (!st.equals("") && !st.equals(" "))
							fwloc.write(st + " ");
				}
				fwloc.write("\n");
			}
		}

		fwloc.close();
		scanner.close();
	}

}
