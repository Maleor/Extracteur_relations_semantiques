# Extracteur de relations sémantiques

Projet de Master informatique dans le domaine du traitement automatique du langage naturel.
L'objectif de ce programme est de rechercher les relations sémantiques présentes dans un texte.

Le programme utilise les données du système JeuxDeMots.org.

## Comment utiliser le système

### Exécution

	java -jar extracteur.jar
		-regles fichier/de/regles
		-cible document/à/analyser
		-comp fichier/mots/composes [optionnel]
		-output dossier/de/sortie
		-verbose(2) [optionnel] : verbose pour afficher dans le terminal le déroulement de l'exécution,
						verbose2 pour plus de détails
		-export_stats [optionnel] : Exporter les statistiques de l'exécution
    
### Les données

<p>*** Le document cible peut être écrit de n'importe quelle manière, cependant, les mots étant analysés via une base de données linguistique, il est nécessaire d'éviter le plus possible les fautes.</p>
<p>*** Le fichier de mots composés n'est pas obligatoire, si vous le donnez en argument, le programme le gérera automatiquement. Dans le fichier, un seul mot composé doit être écrit par ligne.</p>

*** Concernant les règles, elles doivent respecter le format suivant :

contrainte sur ce qui précède ; relation sémantique ; ensemble de mots déclencheur ; contrainte sur ce qui suit

<p>Les contraintes sont optionnelles et portent sur les éléments qui entourent une relation. Elles peuvent imposer le genre, le nombre et la classe grammaticale. 
<p/>Les contraintes sur le nombre sont : Sing ou Plu.
<p/>Les contraintes sur le genre sont : Masc ou Fem.
<p>Toutes les contraintes doivent être séparées par un point, par exemple : Masc.Plu.Adj signifie que l'on recherche un adjectif masculin au pluriel. </p>

Exemples de règles : 

<p/>Sing ; r_carac-1 ; caractérise la ; Sing.Fem ;  
<p/>Sing ; r_carac-1 ; caractérise le ; Sing.Masc ;
<p/>Sing ; r_against ; agit contre la ; Fem.Sing ;
<p/>Sing ; r_against ; agit contre le ; Masc.Sing ;
<p/>Sing ; r_telic_role ; a pour role d' ; ; 
<p/>Sing.Fem ; r_telic_role ; est faite pour ; ;
<p/>Plu.Masc ; r_carac ; sont-ils ; Adj.Plu.Masc ;


# État du code

Actuellement, pour les contraintes, le code ne peut gérer que les adjectifs (Adj) et les verbes (Ver) pour les classes grammaticales, pour les autres cas, un nom sera recherché.
