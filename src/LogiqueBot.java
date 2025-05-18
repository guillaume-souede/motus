import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LogiqueBot {

    // méthode pour filtrer les mots du dictionnaire en fonction des contraintes
    static public ArrayList<String> choix(String progVraie, HashMap<Integer, Character> charsMalPlace, String charImpossible, ArrayList<String> dicoMots) {

        ArrayList<String> outDict = new ArrayList<>(dicoMots); // liste des mots filtrés
        ArrayList<String> foodict = new ArrayList<>(); // liste tampon pour la construction dynamique

        int propositionTaille = progVraie.length();

        // étape 1 : filtrer les mots qui n'ont pas le bon caractère à la bonne position
        for (int i = 0; i < propositionTaille; i++) {
            String charAt = progVraie.charAt(i)+""; // caractère à la position actuelle
            if (!charAt.equals("*")) { // ignorer les positions marquées par un astérisque
                for (String mot : dicoMots) {
                    if (String.valueOf(mot.toUpperCase().charAt(i)).equals(charAt)) {
                        outDict.add(mot); // ajouter les mots qui respectent la contrainte
                    } else {
                        if (outDict.contains(mot) && !String.valueOf(mot.charAt(i)).equals(charAt)) {
                            outDict.remove(mot); // retirer les mots qui ne respectent pas la contrainte
                        }
                    }
                }}
        }

        foodict = new ArrayList<>(outDict); // mise à jour de la liste tampon

        // étape 2 : filtrer les mots qui n'ont pas les bons caractères à la mauvaise position
        if (outDict != null) {
            for (String mot : outDict) {
                for (Character charmalplace : charsMalPlace.values()) {
                    if (testCharMalPlace(mot, charmalplace+"")) { // vérifier si le mot contient les caractères mal placés
                        foodict.add(mot); // ajouter les mots qui contiennent les caractères mal placés
                    } else {
                        foodict.remove(mot); // retirer les mots qui ne respectent pas cette contrainte
                    }
                }
            }
        }

        // étape 3 : fusionner les deux listes (outDict et foodict)
        for (String mot : foodict) {
            outDict.add(mot);
        }

        foodict = new ArrayList<>(outDict); // mise à jour de la liste tampon

        // étape 4 : retirer les mots contenant des caractères impossibles
        for (String mot : foodict) {
            for (int i = 0; i < charImpossible.length(); i++) {
                if (mot.contains(charImpossible.charAt(i) + "")) { // vérifier si le mot contient un caractère interdit
                    outDict.remove(mot); // supprimer le mot
                }
            }
        }

        foodict = new ArrayList<>(outDict); // mise à jour de la liste tampon

        // étape 5 : retirer les mots ayant les caractères mal placés à des positions spécifiques
        for (String mot : foodict) {
            for (Integer posiMal : charsMalPlace.keySet()) {
                if (mot.charAt(posiMal) == charsMalPlace.get(posiMal)) { // vérifier si un caractère mal placé est à une mauvaise position
                    outDict.remove(mot); // supprimer le mot
                }
            }
        }

        return outDict; // retourner la liste des mots filtrés
    }

    // méthode pour tester la présence de caractères mal placés dans un mot
    private static Boolean testCharMalPlace(String mot, String lettres) {
        for (int i = 0; i < lettres.length(); i++) {
            String charActu = lettres.substring(i, i + 1);
            if (!mot.contains(charActu)) { // vérifier si le mot contient le caractère
                return false;
            }
        }
        return true;
    }

    // méthode pour sélectionner un mot aléatoire dans le dictionnaire
    static String randomWord(ArrayList<String> dico) {
        Random random = new Random();
        int randomIndex = random.nextInt(dico.size()); // générer un index aléatoire
        return dico.get(randomIndex); // retourner le mot à cet index
    }

    // charger le motsMotus.txt
    public static ArrayList<String> chargerDictionnaire(String cheminFichier, int longueurMot) {
        OuvrirDB db = new OuvrirDB(cheminFichier);
        return new ArrayList<>(db.getOnePhrase(longueurMot));
    }

    public static void main(String[] args) {
        OuvrirDB db = new OuvrirDB("data/rules.txt"); // ouvrir la base de données
        ArrayList<String> dico = new ArrayList<>(db.getOnePhrase(6)); // récupérer une liste de mots
        HashMap<Integer, Character> charsMalPlace = new HashMap<>();
        charsMalPlace = EtatMot.checkWrongPlacement2("partir", "patate"); // vérifier les caractères mal placés
        choix("pa****", charsMalPlace, "zrq", dico); // appeler la méthode choix
    }
}