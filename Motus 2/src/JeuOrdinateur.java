import java.util.*;

public class JeuOrdinateur {
    private static final int essaisMax = 6;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void ordinateurDevine(String motSecret, Scanner scanner) {
        String[] dictionnaire = JeuJoueur.getListeMotsPossibles();
        List<String> motsPossibles = new ArrayList<>(Arrays.asList(dictionnaire));
        motsPossibles.removeIf(m -> m.length() != motSecret.length());

        if (motsPossibles.isEmpty()) {
            System.out.println("ℹ Mot impossible à jouer.");
            return;
        }

        Set<String> motsEssayes = new HashSet<>();
        Random random = new Random();
        MotATrouver motATrouver = new MotATrouver(motSecret);

        int essais = 0;
        while (essais < essaisMax && !motsPossibles.isEmpty()) {
            String proposition;
            do {
                proposition = motsPossibles.get(random.nextInt(motsPossibles.size()));
            } while (motsEssayes.contains(proposition));

            motsEssayes.add(proposition);
            System.out.println("Essai " + (essais + 1) + " : L'ordinateur propose " + GRAS + proposition + RESET);
            motATrouver.afficherEtatMot(proposition);

            if (motATrouver.verifierProposition(proposition)) {
                System.out.println("🥳 ⓂⓄⓉⓊⓈ de l'ordinateur en " + (essais + 1) + " essai(s) !");
                return;
            }

            motsPossibles.removeIf(mot -> !JeuOrdinateur.correspondAEtat(mot, motATrouver.getEtatMot()));
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
