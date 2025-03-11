import java.util.*;

public class JeuOrdinateur {
    private static final int essaisMax = 6;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void ordinateurDevine(String motSecret, Scanner scanner, OuvrirDB db) {
        ArrayList<String> dictionnaire = db.getPhrase().get(motSecret.length()).line; 
        // prend tout les mots de taille a rechercher

        if (dictionnaire.isEmpty()) {
            System.out.println("ℹ Mot impossible à jouer.");
            return;
        }

        Set<String> motsEssayes = new HashSet<>();
        Random random = new Random();
        MotATrouver motATrouver = new MotATrouver(motSecret);

        int essais = 0;
        while (essais < essaisMax && !dictionnaire.isEmpty()) {
            String proposition;
            do {
                proposition = dictionnaire.get(random.nextInt(dictionnaire.size()));
            } while (motsEssayes.contains(proposition));

            motsEssayes.add(proposition);
            System.out.println("Essai " + (essais + 1) + " : L'ordinateur propose " + GRAS + proposition + RESET);
            motATrouver.afficherEtatMot(proposition);

            if (motATrouver.verifierProposition(proposition)) {
                System.out.println("🥳 ⓂⓄⓉⓊⓈ de l'ordinateur en " + (essais + 1) + " essai(s) !");
                return;
            }

            dictionnaire.removeIf(mot -> !JeuOrdinateur.correspondAEtat(mot, motATrouver.getEtatMot()));
            essais++;
        }

        System.out.println("😢 Défaite après " + essaisMax + " essais.");
    }

    private static boolean correspondAEtat(String mot, char[] etatMot) {
        for (int i = 0; i < etatMot.length; i++) {
            if (etatMot[i] == '_') continue;
            if (Character.isUpperCase(etatMot[i]) && mot.charAt(i) != Character.toLowerCase(etatMot[i])) return false;
            if (Character.isLowerCase(etatMot[i]) && !mot.contains(String.valueOf(etatMot[i]))) return false;
        }
        return true;
    }
}
