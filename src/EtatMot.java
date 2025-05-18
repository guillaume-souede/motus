import java.util.ArrayList;
import java.util.HashMap;

public class EtatMot {
    /*
     * à partir d'une proposition test quels sont les bons caractère
     * renvoie 2 String : 
     *  - Le premier composé de lettres aux bons endroits et '*' aux mauvais endroit.
     *  - Les secondRenvoie aussi les char mal placé
     */


    static public String checkEtatMot(String proposition, String motSecret){
        // étape 1 création du premier String 
        String SilhouetteMot = "";                   // la construction du premier String
        char lettreActu;                            // le char a vérifier
        for (int i = 0; i < proposition.length(); i++) {
            if (proposition.charAt(i) == motSecret.charAt(i)) {
                lettreActu = proposition.charAt(i); // si bon => met dans la chaine le char
                SilhouetteMot += lettreActu;         // la concaténation
            } else {
                SilhouetteMot += '*';                //si pas bon met une étoile
            }
        }
        return SilhouetteMot;
    }

        // étape 2 création du String de mots mal placé
    static public String checkWrongPlacement(String proposition, String motSecret){
        String silouetteMot = checkEtatMot(proposition, motSecret);
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
            propositionTrimmed += proposition.charAt(positions)+""; // techniquement le +"" n'est pas nécéssaire pour la concaténation
            motSecretTrimmed += motSecret.charAt(positions)+"";
        }

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

    static String emojiRepresentation(String proposition, String motSecret) {
        // passer de StringBuilder à juste String car plus simple a comprendre
        String affichageEmoji = "";
        for (int i = 0; i < motSecret.length(); i++) {
            char lettre = proposition.charAt(i);
            if (lettre == motSecret.charAt(i)) {
                affichageEmoji +=("🟦");
            } else if (motSecret.contains(String.valueOf(lettre))) {
                affichageEmoji+=("🟡");
            } else {
                affichageEmoji+=("🟥");
            }
        }
        return affichageEmoji;
    }

    static String extractImpossiblechar(String proposition, String motSecret){
        // legacy function
        String outString = ""; // stocke les char impossible pour le mot
        for (int i = 0; i < proposition.length(); i++) {
            if (motSecret.contains(proposition.charAt(i)+"") == false) {
                outString += proposition.charAt(i);
            }
        }
        return outString;
    }

    static HashMap<Integer,Character> checkWrongPlacement2(String motSecret,String proposition ){
        HashMap<Integer,Character> mauvaisePosBonChar = new HashMap<>();
        for (int i = 0; i < proposition.length(); i++) {
            if (motSecret.contains(proposition.charAt(i)+"") == true && proposition.charAt(i) != motSecret.charAt(i)) { // si le char est dans le mot secret mais pas à la bonne position
            mauvaisePosBonChar.put(i, proposition.charAt(i)) ; // on ajoute la position en clef et le char mal plaé en valeur
            }
        }
        return mauvaisePosBonChar;
    }

    static void pprint(String proposition, String motSecret){
        System.out.println(checkEtatMot(proposition, motSecret));
        System.out.println("lettres malplacées : "+checkWrongPlacement(proposition, motSecret));
        System.out.println(emojiRepresentation(proposition, motSecret));
    }




    public static String getImpossibleChars(String motSecret, String proposition) {
        StringBuilder impossibleChars = new StringBuilder();
        for (char c : proposition.toCharArray()) {
            if (!motSecret.contains(String.valueOf(c))) {
                impossibleChars.append(c);
            }
        }
        return impossibleChars.toString();
    }

    public static String updateProgVraie(String motSecret, String proposition) {
        char[] progArray = new char[motSecret.length()];
        for (int i = 0; i < motSecret.length(); i++) {
            if (proposition.charAt(i) == motSecret.charAt(i)) {
                progArray[i] = motSecret.charAt(i);
            } else {
                progArray[i] = '*'; // Placeholder for unknown characters
            }
        }
        return new String(progArray);
    }
        public static void main(String[] args) {
        // bloc de test
        String propostion= "partir";
        String motSecret = "patate";
        System.out.println(checkEtatMot(propostion, motSecret));
        System.out.println(checkWrongPlacement(propostion, motSecret));
        System.out.println(emojiRepresentation(propostion, motSecret));
    }
}