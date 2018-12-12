# Extracteur de relations sémantiques

Projet de Master informatique dans le domaine du traitement automatique du langage naturel.
L'objectif de ce programme est de rechercher les relations sémantique présentes dans un texte.

Le programme utilise les données du système JeuxDeMots.org.

## Comment utiliser le système

### Exécution

	java -jar extracteur.jar
		-regles fichier/de/regles
		-cible document/à/analyser
		-output dossier/de/sortie
		-verbose [optionnel] : Afficher dans le terminal le déroulement de l'exécution
		-export_stats [optionnel] : Exporter les statistiques de l'exécution
    
### Les données

Concernant les règles, elles doivent respecter le format suivant :

contrainte sur ce qui précède ; relation sémantique ; ensemble de mots déclencheurs ; contrainte sur ce qui suit

Les contraintes sont optionnelles est portent sur les éléments qui entourent une relation. Elles peuvent imposer le genre, le nombre et la classe grammaticale. Toutes les contraintes doivent être séparées par un point, par exemple : Masc.Plu.Adj

Exemples de règles : 

<p/>Sing ; r_carac-1 ; caractérise la ; Sing.Fem ;  
<p/>Sing ; r_carac-1 ; caractérise le ; Sing.Masc ;
<p/>Sing ; r_against ; agit contre la ; Fem.Sing ;
<p/>Sing ; r_against ; agit contre le ; Masc.Sing ;
<p/>Sing ; r_telic_role ; a pour role d' ; ; 
<p/>Sing.Fem ; r_telic_role ; est faite pour ; ;
<p/>Plu.Masc ; r_carac ; sont-ils ; Adj.Plu.Masc ;


# État du code

Actuellement, pour les contraintes, le code ne peut gérer que les adjectifs pour les classes grammaticales, pour les autres cas, un nom sera recherché.
