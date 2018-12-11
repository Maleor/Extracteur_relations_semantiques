package analyserMots;

import java.util.ArrayList;

import requeterRezo.Filtre;
import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class AnalyseurMotSeul {

	public AnalyseurMotSeul() {

	}

	//////////////////////
	/** PUBLIC METHODS **/
	//////////////////////

	/**
	 * Analyse un Mot est vérifie s'il respecte toutes les contraintes données
	 * 
	 * @param cible
	 *            Le Mot que l'on analyse
	 * 
	 * @param contrainte
	 *            Contraintes qui portent sur le Mot à analyser (les contraintes
	 *            sont séparées par un point)
	 * 
	 * @return Le poids de la relation si le Mot respecte la contrainte, -1 sinon.
	 *         Par exemple, si le Mot doit etre un determinant, la méthode retourne
	 *         le poids de la relation vers la catégorie grammaticale "Det".
	 */
	public int respecteContrainte(Mot cible, String contrainte) {

		ArrayList<Voisin> voisins = new ArrayList<>();

		ArrayList<String> contraintes = recupererContraintes(contrainte);

		voisins = cible.getRelations_sortantes(4);

		int toRet = -1;

		if (voisins != null) {

			for (String cont : contraintes) { // pour chaque contrainte
				for (Voisin v : voisins) { // on viste chaque relation sortante du Mot

					/*
					 * Nous faisons le choix ici de ne pas garder les mots qui peuvent être un
					 * determinant, considérant que si un determinant est présent, alors un Mot
					 * faisant office de Nom est présent pas loin. De toute évidence, ce n'est pas
					 * un déterminant qui accompagnera un pattern.
					 */
					if (v.getNom().contains("Det"))
						return -1;

					/*
					 * Ne respecte pas la contrainte s'il y a une contradiction dans le genre ou
					 * dans le nombre
					 */
					if ((v.getNom().contains("Plu") && cont.equals("Sing"))
							|| (v.getNom().contains("Sing") && cont.equals("Plu"))
							|| (v.getNom().contains("Masc") && cont.equals("Fem"))
							|| (v.getNom().contains("Fem") && cont.equals("Masc")))
						return -1;

					if (v.getNom().contains(cont) && !cont.equals("Plu") && !cont.equals("Masc") && !cont.equals("Sing")
							&& !cont.equals("Fem"))
						if (v.getPoids() > toRet)
							toRet = v.getPoids();
				}
			}
		}
		return toRet;
	}

	/**
	 * Crée un Mot à partir d'un string et vérifie si ca peut etre un nom
	 * 
	 * @param word
	 *            Le string à partir duquel on crée un Mot
	 * 
	 * @return Le poids de la relation si le Mot peut etre un nom, -1 sinon.
	 */
	public int analyseWord(String word, String contrainte, RequeterRezoDump systeme) {
		if (word.equals(""))
			return -1;

		int toRet;

		Mot m = systeme.requete(word, 4, Filtre.FiltreRelationsEntrantes);

		if (contrainte.equals("") || contrainte.equals(" "))
			contrainte = "Nom";

		toRet = (m != null) ? respecteContrainte(m, contrainte) : -1;

		return toRet;
	}

	///////////////////////
	/** PRIVATE METHODS **/
	///////////////////////

	/**
	 * Dans un seul String, plusieurs contraintes peuvent être présentes séparées
	 * par des points. Cette méthode récupère ces différentes contraintes.
	 * 
	 * @param contraintes
	 *            Le String contenant les contraintes
	 * 
	 * @return La liste de contraintes
	 */
	public ArrayList<String> recupererContraintes(String contraintes) {

		ArrayList<String> toRet = new ArrayList<>();

		if (contraintes.contains(".")) {

			String[] split = contraintes.split("[.]+");

			for (String str : split) {

				switch (str.toLowerCase()) {

				case "plu":
				case " plu":
				case "plu ":
				case " plu ":
					str = "Plur";
					break;

				case "sing":
				case " sing":
				case "sing ":
				case " sing ":
					str = "Sing";
					break;

				case "masc":
				case " masc":
				case "masc ":
				case " masc ":
					str = "Mas";

				case "fem":
				case " fem":
				case "fem ":
				case " fem ":
					str = "Fem";
					break;

				case "adj":
				case " adj":
				case "adj ":
				case " adj ":
					str = "Adj";
					break;

				default:
					break;

				}
				toRet.add(str);
			}
		} else if (!contraintes.contains("Adj") && !contraintes.contains("adj")) {
			toRet.add("Nom");
			toRet.add(contraintes);
		} else
			toRet.add(contraintes);

		return toRet;
	}

}
