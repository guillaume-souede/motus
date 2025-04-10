import java.util.ArrayList;

public class LogiqueBot {

    static public ArrayList<String> choix(String progVraie, String charsMalPlace, ArrayList<String> dicoMots){
        /* si on injecte directement des objets OuvrirDb (pas bien) */
        // // Extraction de la taille du Mot
        // int taille =  progVraie.length();
        // // Extraction des mots de taille égale au mot cherché
        // Mots phrase = dicoMots.getPhrase().get(taille);
        // Passage au format Liste dynamique

        // FILTRAGE DES MOTS IMPOSSIBLES EN 2 ETAPES : 
        // étape 1 : filtrer les mots qui n'ont pas le bon caractère à la bonne position
        for (int i = 0; i < dicoMots.size(); i++) {
            // Stockage du caractère (!attention!) à la position actuelle
            char charAt = progVraie.charAt(i);
            // Test si le caractère est une lettre ou un astérisque (*)
            if (charAt != '*'){
                // Retrait des mots qui n'ont pas le caractère à la bonne position
                for (String mot : dicoMots) {
                    if (mot.charAt(i) != charAt) {dicoMots.remove(mot);}
                }
            }
        }

        // étape 2 : filtrer les mots qui n'ont pas le bon caractère à la mauvaise position
        if (dicoMots != null) {
            for (String mot : dicoMots) {
                if (testCharMalPlace(mot, charsMalPlace) != true){
                    dicoMots.remove(mot);  
                }
            }
        }
        // ya eu filtrage des mots impossibles, retour a vous la télé
        // Ici la télé, merci Jan.
            return dicoMots;
        }

    // Test de la présence de caractères mal placés
    private static Boolean testCharMalPlace (String mot, String lettres){
        for (int i = 0; i < lettres.length(); i++) {
            String charActu = lettres.substring(i, i+1);
            // test si le mot contient bien le caractère
            if (mot.contains(charActu) == false) {
                    return false;
                }
            
        }
        return true;
    }

}