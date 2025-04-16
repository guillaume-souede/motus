import java.util.ArrayList;
import java.util.HashMap;

public class LogiqueBot {

    static public ArrayList<String> choix(String progVraie, HashMap<Integer,Character> charsMalPlace, String charImpossible, ArrayList<String> dicoMots){

        ArrayList<String> outDict = new ArrayList<>(); // la sortie
        ArrayList<String> foodict = new ArrayList<>(); // sert de liste tampon à outdict pour pouvoir la construire dynamiquement 
                                                       //outDict tout en lisant sa dernère image

        int propositionTaille = progVraie.length();

        // FILTRAGE DES MOTS IMPOSSIBLES EN 2 ETAPES : 
        // étape 1 : filtrer les mots qui n'ont pas le bon caractère à la bonne position
        for (int i = 0; i < propositionTaille; i++) {
            // Stockage du caractère (!attention!) à la position actuelle
            char charAt = progVraie.charAt(i);
            // Test si le caractère est une lettre ou un astérisque (*)
            if (charAt != '*'){
                /*  Retrait des mots qui n'ont pas le caractère à la bonne position 
                 *  /!\ la modification du dico pendent la boucle est strictement interdit
                 *  nous allons donc crée un nouveau dico (nouvelle liste car dicon non pertinant)
                */ 
                for (String mot : dicoMots) {
                    // nécécité de faire une boucle pour la taille du mot car Jan regarder la taille du dict de 3000 c'est pas possible
                    if (mot.charAt(i) == charAt) {
                        outDict.add(mot);}
                    else {
                        if (outDict.contains(mot) && mot.charAt(i) != charAt) { // si ya une lettre fixe qui existe pas exécution
                            outDict.remove(mot);}
                    }
                }
            }
        }

        // étape 2 : filtrer les mots qui n'ont pas le bon caractère à la mauvaise position
        /* conséquence de mon ignorance, on ajoutera plusieurs fois les mots ayant plus de lettre
         * en eux ==> aventage donne un poids :)
         */

        if (outDict != null) {
            for (String mot : outDict) {
                // mettre en stockage les lettres mal placé sans les position (legacy)
                String charMalPlaceeString = charsMalPlace.values().toString();
                if (testCharMalPlace(mot, charMalPlaceeString) == true){
                    foodict.add(mot);}
                else {foodict.remove(mot);}
            }
        }

        // étape 3 : fusionner les 2 dico. Pas parfais car cherche pas a mettre toutes les lettres manquantes
        for (String mot : foodict) {
            outDict.add(mot);
        }
        
        foodict = new ArrayList<>(outDict); // mise en cache de outDict

        // ya eu filtrage des mots impossibles, retour à vous la télé
        // Ici la télé, merci Jan.
        // je suis Jan retour à vous Guillaume

        // étape 4 extraire les lettres innexistante du mot et retirer toutes les possibilitées
        for (String mot : foodict) {
            for (int i = 0; i < charImpossible.length(); i++) {
                if (mot.contains(charImpossible.charAt(i)+"")) {// test si le mot à un char impossible
                    outDict.remove(mot);                        // suppression
                }
            }
        }
        
        foodict = new ArrayList<>(outDict);

        // étape 5 : filtrer les mots ayant les lettres mal placées
        for (String mot : foodict) {
            // prendre les position des lettres mal placé 
            for (Integer posiMal : charsMalPlace.keySet()) {
                if (mot.charAt(posiMal) == charsMalPlace.get(posiMal)) {// test si à la position mauvaise ya le Char mal placé
                    outDict.remove(mot);
                }
            }
            
        }
            return outDict;
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



    public static void main(String[] args) {
        OuvrirDB db = new OuvrirDB();
        ArrayList<String> dico = new ArrayList<>(db.getOnePhrase(6));
        HashMap<Integer,Character> charsMalPlace = new HashMap<>();
        charsMalPlace = EtatMot.checkWrongPlacement2("partir", "patate");
        choix("pa****", charsMalPlace, "zrq", dico);
    }
}