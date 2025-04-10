public class EtatMot {
    
    String motSecret;

    /*
     * à partir d'une proposition test quels sont les bons caractère
     * renvoie 2 String : 
     *  - Le premier composé de lettres aux bons endroits et '*' aux mauvais endroit.
     *  - Les secondRenvoie aussi les char mal placé
     */

    
    public String checkEtatMot(String proposition){
        // étape 1 création du premier String 

        String occurrencesTemp = null;              // la construction du premier String
        char lettreActu;                            // le char a vérifier
        for (int i = 0; i < motSecret.length(); i++) {
            if (proposition.charAt(i) == motSecret.charAt(i)) {
                lettreActu = proposition.charAt(i); // si bon => met dans la chaine le char
                occurrencesTemp += lettreActu;      // la concaténation
            } else {
                occurrencesTemp += '*';             //si pas bon met une étoile
            }
        }
        return occurrencesTemp;
    }

        // étape 2 création du String de mots mal placé
    public String checkWrongPlacement(String proposition){
        for (int i = 0; i < proposition.length(); i++) {
            String lettreActu = proposition.charAt(i)+""; // le `+ ""` == toString()
            
        }
    }


}