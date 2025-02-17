import java.util.*;

public class JeuJoueur {
    private static final int essaisMax = 6;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void joueurDevine(Scanner scanner) {
        String[] motsPossibles = getListeMotsPossibles();
        Random random = new Random();
        String motSecret = motsPossibles[random.nextInt(motsPossibles.length)];
        MotATrouver motATrouver = new MotATrouver(motSecret);

        System.out.println("🫵 Vous devez deviner un mot de " + GRAS + motSecret.length() + " lettres en " + essaisMax + " tentatives" + RESET + ".");

        for (int essai = 1; essai <= essaisMax; essai++) {
            System.out.print("Essai " + essai + "/" + essaisMax + " : ");
            String proposition = scanner.nextLine().toLowerCase();

            if (proposition.length() != motSecret.length()) {
                System.out.println("Le mot doit contenir " + motSecret.length() + " lettres.");
                essai--;
                continue;
            }

            motATrouver.afficherEtatMot(proposition);

            if (motATrouver.verifierProposition(proposition)) {
                System.out.println("🥳 Félicitations !" + GRAS + "ⓂⓄⓉⓊⓈ en " + essai + " essai(s) sur " + essaisMax + RESET + ".");
                return;
            }
        }
        System.out.println("😥 Perdu ! Le mot était : " + motSecret);
    }

    public static String[] getListeMotsPossibles() {
        return new String[]{"avion", "voiture", "maison", "ordinateur", "bureau", "table", "chaise", "fenetre"};
    }
}
