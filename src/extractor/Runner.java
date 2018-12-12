package extractor;

import java.io.IOException;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Runner {

	public static void main(String[] args) throws IOException {

		if ((args.length < 3) || (args.length == 1 && args[0].equals("--help"))) {
			System.err.println(
					"Usage : -regles 'fichier/de/regles' -cible 'document/à/analyser' -output 'dossier/de/sortie' -verbose -export_stats\n");
			System.err.println("Fichier de regles : Chemin vers le fichier qui contient les règles");
			System.err.println("Document à analyser : Le chemin vers le document texte à analyser");
			System.err.println("Fichier de sortie : Le chemin vers le dossier qui contient les fichiers d'export");
			System.err.println("Verbose (Optionnel) : Afficher dans le terminal le déroulement de l'exécution");
			System.err.println("Export_stats (Optionnel) : Exporter les statistiques de l'exécution");
			return;
		}

		String[] args2 = new String[5];

		args2[3] = "0";
		args2[4] = "0";

		for (int index = 0; index < args.length; index++) {

			if (args[index].equals("-queries"))
				args2[0] = args[index + 1];

			if (args[index].equals("-cible"))
				args2[1] = args[index + 1];

			if (args[index].equals("-output"))
				args2[2] = args[index + 1];

			if (args[index].equals("-verbose"))
				args2[3] = "1";

			if (args[index].equals("-export_stats"))
				args2[4] = "1";
		}

		Parser parser = new Parser(args2);

		if (args2[3] == "1") {
			System.out.println("######################################");
			System.out.println("####### EXECUTION DU PROGRAMME #######");
			System.out.println("######################################\n");
		}

		parser.run();

		if (args2[3] == "1") {
			System.out.println("\n################################");
			System.out.println("####### FIN DU PROGRAMME #######");
			System.out.println("################################");
		}

	}

}
