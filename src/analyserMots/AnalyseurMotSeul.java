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
	 * Analyse un Mot est vérifie s'il peut etre un nom
	 * 
	 * @param cible
	 *            Le Mot que l'on analyse
	 * 
	 * @return Le poids de la relation si le Mot peut etre un nom, -1 sinon.
	 */
	public int respecteContrainte(Mot cible, String contrainte) {

		ArrayList<Voisin> voisins = new ArrayList<>();
		voisins = cible.getRelations_sortantes(4);
		int toRet = -1;
		
		if (voisins != null) {
			for (Voisin v : voisins) {
				
				if(v.getNom().contains("Det"))
					return -1;
				
				if (v.getNom().contains(contrainte)) {
					if(v.getPoids() > toRet) {
						toRet = v.getPoids();
					}
				}
			}
		}

		return toRet;
	}

}
