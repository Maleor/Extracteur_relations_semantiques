package template;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Template {
	/* Un template contient une relation cible et une expression en langage naturel */
	private String relation; // exemple : r_isa
	private String eln; // exemple : est un
	private String contraintePost; //contrainte sur ce qui suit, exemple : adj
	private String contrainteAnte; //contrainte sur ce qui precede, exemple : nom
	private int t_length; // la longueur du template : le nombre de mots dans son expression en langage naturel
	
	public Template(String relation, String eln, String contrainteAnte, String contraintePost){
		this.relation = relation;
		this.eln = eln;
		this.contraintePost = contraintePost;
		this.contrainteAnte = contrainteAnte;
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
	
	public String getContraintePost() {
		return this.contraintePost;
	}
	
	public String getContrainteAnte() {
		return this.contrainteAnte;
	}
	
	
	/* Retroune le nombre de mots dans une phrase ou les mots sont séparés par un ou plusieurs espaces */
	public static int wordCount(String s){
	    return s.trim().split("\\s+").length;
	}
	
	/* Retourne une string qui explique le template */
	public String get_template_under_string_shape() {
		return (this.get_eln() + " --> " + this.get_relation() + " : " + this.get_t_length());
	}
	
	
}