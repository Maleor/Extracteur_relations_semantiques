package analyserMots;

import java.util.ArrayList;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class AnalyseurMotsComposes {
	
	private PatriciaTrie<Integer> mots_composes;
	
	public AnalyseurMotsComposes(PatriciaTrie<Integer> mots_composes) {
		this.mots_composes = mots_composes;
	}
	
	
	/**
	 * Vérifie si un mot composé est présent dans une chaine de caractères
	 * 
	 * @param str
	 *            Chaine de caracteres analysée
	 * 
	 * @return Le mot composé s'il y en a un, chaine de caractères vide sinon
	 */
	public String nomCompose(String str) {

		String toRet = "";

		String[] wd = str.split(" ");
		String tmpString = "";

		ArrayList<String> mots = new ArrayList<>();

		for (String s : wd) {
			if (!s.equals(""))
				mots.add(s);
		}

		for (int index = mots.size(); index > 0; index--) {

			tmpString = "";

			for (int jndex = mots.size() - index; jndex < mots.size(); jndex++) {
				tmpString = tmpString + " " + mots.get(jndex);
			}

			tmpString = tmpString.substring(1, tmpString.length());

			if (mots_composes.containsKey(tmpString)) {
				toRet = tmpString;
				break;
			}
		}
		return toRet;
	}
	
	

}
