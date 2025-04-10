import java.util.ArrayList;

public class EtatMot {
    
    String motSecret;

    /*
     * à partir d'une proposition test quels sont les bons caractère
     * renvoie 2 String : 
     *  - Le premier composé de lettres aux bons endroits et '*' aux mauvais endroit.
     *  - Les secondRenvoie aussi les char mal placé
     */

    
    static public String checkEtatMot(String proposition){
        // étape 1 création du premier String 

        String SilouetteMot = null;              // la construction du premier String
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

        // a. get les positions non justes
        for (int i = 0; i < silouetteMot.length(); i++) {
            if (silouetteMot.charAt(i) != '*'){
                positionFausses.add(i);
            }
        }// en sortie on a les position fausses dasn positionsFausses

        // b. 


        for (int i = 0; i < proposition.length(); i++) {
            String lettreActu = proposition.charAt(i)+""; // le `+ ""` == toString()
            
        }
    }


}