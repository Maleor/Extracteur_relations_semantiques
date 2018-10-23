package template;

public class Template {
	/* Un template contient une relation cible et une expression en langages naturel */
	private String relation; // exemple : r_isa
	private String eln; // exemple : est un
	private int t_length; // la longueur du template : le nombre de mots dans son expression en langages naturel
	
	public Template(String relation, String eln){
		this.relation = relation;
		this.eln = eln;
		this.t_length = wordCount(eln);
	}

	public String get_relation() {
		return this.relation;
	}
	
	public String get_eln() {
		return this.eln;
	}
	
	public int get_t_length() {
		return this.t_length;
	}
	
	/* Retroune le nombres de mots dans une phrase ou les mots sont séparé par un ou plusieurs espaces */
	public static int wordCount(String s){
	    return s.trim().split("\\s+").length;
	}
	
	/* Retourne une string qui explique le template */
	public String get_template_under_string_shape() {
		return (this.get_eln() + " --> " + this.get_relation() + " : " + this.get_t_length());
	}
}