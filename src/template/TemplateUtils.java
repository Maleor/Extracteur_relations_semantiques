package template;

import java.util.ArrayList;

public class TemplateUtils {

	public TemplateUtils() {

	}

	/**
	 * Vérifie si un chaine de caractère existe comme template
	 * 
	 * @param t
	 *            Le template avec lequel on compare la chaine de caractères
	 * 
	 * @param word
	 *            La chaine de caractère que l'on compare
	 * 
	 * @return True si la chaine de caractères est un template, false sinon.
	 */
	public boolean t_match(Template t, String word) {
		return t.get_eln().equals(word);
	}

	/**
	 * Regroupe les mots utilisés dans un template avec des '_' de façon à ce que
	 * les mots à l'interieur ne soit pas utilisés pour d'autres templates plus
	 * petits.
	 * 
	 * Exemple : Si le template "est un" est trouvé, on supprime le mot "un" et on
	 * remplace "est" par "est_un".
	 *
	 * @param words
	 *            La liste de mots initiale dans laquelle on regroupe les mots
	 *
	 * @param begin
	 *            L'emplacement dans le texte où le template commence
	 * 
	 * @param window_size
	 *            Le nombre de mots qui doivent etre regroupés, correspond à la
	 *            taille du template
	 * 
	 * @return La nouvelle liste dans laquelle les mots ont été regroupés
	 * 
	 */
	public void protectTemplate(ArrayList<String> words, int begin, int window_size) {

		String protectedTemplate = "";


		for (int index = begin; index < begin + window_size; index++)
			protectedTemplate = protectedTemplate + "_" + words.get(index);

		for (int index = begin; index < begin + window_size -1; index++)
			words.remove(begin+1);

		words.set(begin, protectedTemplate.substring(1, protectedTemplate.length()));


	}

}
