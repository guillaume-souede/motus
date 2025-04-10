import java.util.*;

public class JeuJoueur {
    private static final int essaisMax = 6;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void joueurDevine(Scanner scanner, OuvrirDB db) {
        ArrayList<String> listMot = new ArrayList<>();
        Random random = new Random();
        int tailleMots = random.nextInt(3)+6;   // get random length
        listMot = db.getAllPhrase().get(tailleMots).line;  
        String motSecret = listMot.get(random.nextInt(listMot.size())); // get random mot
        MotATrouver motATrouver = new MotATrouver(motSecret);
            // la on a ouvert la db, et choisis un mot au hazard
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

    /* 
    public static String[] getListeMotsPossibles() {
        // a modifier pour intégrer les Arraylist et l'ouverture de la DB de mots 
        // TLDR modifié et retrouvé dans OuvrirDB et Mots
        return new String[]{"avion", "voiture", "maison", "ordinateur", "bureau", "table", "chaise", "fenetre"};
    }
    */
}
