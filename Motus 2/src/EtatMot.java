import java.util.ArrayList;

public class EtatMot {
    
    static String motSecret;

    /*
     * à partir d'une proposition test quels sont les bons caractère
     * renvoie 2 String : 
     *  - Le premier composé de lettres aux bons endroits et '*' aux mauvais endroit.
     *  - Les secondRenvoie aussi les char mal placé
     */

    
    static public String checkEtatMot(String proposition){
        // étape 1 création du premier String 

        String SilouetteMot = "";              // la construction du premier String
        char lettreActu;                            // le char a vérifier
        for (int i = 0; i < motSecret.length(); i++) {
            if (proposition.charAt(i) == motSecret.charAt(i)) {
                lettreActu = proposition.charAt(i); // si bon => met dans la chaine le char
                SilouetteMot += lettreActu;      // la concaténation
            } else {
                SilouetteMot += '*';             //si pas bon met une étoile
            }
        }
        return SilouetteMot;
    }


        // étape 2 création du String de mots mal placé
    static public String checkWrongPlacement(String proposition){
        String silouetteMot = checkEtatMot(proposition);
        ArrayList<Integer> positionFausses= new ArrayList<Integer>();
        String motMalPlace = "";

        // a. get les positions non justes
        for (int i = 0; i < silouetteMot.length(); i++) {
            if (silouetteMot.charAt(i) == '*'){
                positionFausses.add(i);
            }
        }// en sortie on a les position fausses dasn positionsFausses

        // b. sortir les 2 chars des fausses positions
        String propositionTrimmed = "", motSecretTrimmed = "";
        for (int positions : positionFausses) {
            propositionTrimmed += proposition.charAt(positions)+""; // techniquement le +"" n'est pas nécécaire pour la concaténation
            motSecretTrimmed += motSecret.charAt(positions)+"";
        }

        // //c. final, chercher si la lettre de la proposition existe mais mal placé
        // for (int i = 0; i < propositionTrimmed.length(); i++) {
        //     char tempChar = propositionTrimmed.charAt(i);
        //     for (int j = 0; j < motSecretTrimmed.length(); j++) {
        //         if (tempChar == motSecretTrimmed.charAt(j)) {
        //             motMalPlace += tempChar;                        // exemple de concaténation sans le //
        //         }
        //     }
        // }

        // avec un while pour un code de sortie
        for (int i = 0; i < propositionTrimmed.length(); i++) {
            char tempChar = propositionTrimmed.charAt(i);
            boolean pasTrouve = true;           int j = 0;
            while ( j < motSecretTrimmed.length() && pasTrouve) {
                if (tempChar == motSecretTrimmed.charAt(j)) {
                    motMalPlace += tempChar;
                    pasTrouve = false;
                }
                j ++;
            }
        }

        return motMalPlace;
    
    }

    public static void main(String[] args) {
        motSecret = "patate";
        checkWrongPlacement("partir");
    }

}