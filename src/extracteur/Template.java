package extracteur;

public class Template {
	
	private String objet;
	private String sujet;
	
	public Template(String sujet, String objet){
		this.objet = objet;
		this.sujet = sujet;
	}

	public String getObjet() {
		return this.objet;
	}
	
	public String getSujet() {
		return this.sujet;
	}
}