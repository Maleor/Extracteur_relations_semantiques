package analyserMots;

import java.util.ArrayList;

import requeterRezo.Mot;
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

	/**
	 * Analyse un Mot est vérifie s'il respecte une contrainte données
	 * 
	 * @param cible
	 *            Le Mot que l'on analyse
	 * 
	 * @param contrainte
	 *            Contrainte qui porte sur le Mot à analyser
	 * 
	 * @return Le poids de la relation si le Mot respecte la contrainte, -1 sinon.
	 *         Par exemple, si le Mot doit etre un determinant, la méthode retourne
	 *         le poids de la relation vers la catégorie grammaticale "Det".
	 */
	public int respecteContrainte(Mot cible, String contrainte) {

		ArrayList<Voisin> voisins = new ArrayList<>();

		voisins = cible.getRelations_sortantes(4);

		int toRet = -1;

		if (voisins != null) {
			for (Voisin v : voisins) {

				/*
				 * Nous faisons le choix ici de ne pas garder les mots qui peuvent être un
				 * determinant, considérant que si un determinant est présent, alors un Mot
				 * faisant office de Nom est présent pas loin. De toute évidence, ce n'est pas
				 * un déterminant qui accompagnera un pattern.
				 */
				if (v.getNom().contains("Det"))
					return -1;

				if (v.getNom().contains(contrainte))
					if (v.getPoids() > toRet)
						toRet = v.getPoids();
			}
		}
		return toRet;
	}

}
