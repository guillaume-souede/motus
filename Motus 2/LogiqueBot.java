import java.util.ArrayList;

public class LogiqueBot {

    
    public ArrayList<String> choix(String progVraie, String charsMalPlace, OuvrirDB dicoMots){
        int taille =  progVraie.length(); // extraction de la taille

        Mots phrase = dicoMots.getPhrase().get(taille); // extraction des mots de taille équivalente au mot cherché
        
        ArrayList<String> sousList = phrase.getLine(); // mise sous forme de Liste dynamique


        for (int i = 0; i < taille; i++) {
            char charAt = progVraie.charAt(i); // stockage du char a la position actuelle

            if (charAt != '*'){
                for (String mot : sousList) {
                    if (mot.charAt(i) != charAt) {sousList.remove(mot);} // on retire les mots n'ayant pas le char a la bonne position
                }
            }
        }
        // étape 2 éliminer tous ceux qui n'ont pas 

        if (sousList != null) {
            for (String mot : sousList) {
                if (testCharMalPlace(mot, charsMalPlace) != true){
                    sousList.remove(mot); // si un des mots n'a pas  
                }
            }
        }
        // ya eu filtrage des mots impossibles, retour a vous la télé
            return sousList;
        }

    private Boolean testCharMalPlace (String mot, String lettres){
        for (int i = 0; i < lettres.length(); i++) {
            String charActu = lettres.substring(i, i+1);
            if (mot.contains(charActu) == false) { // test si le mot contient les lettres
                    return false;
                }
            
        }
        return true;
    }

}

