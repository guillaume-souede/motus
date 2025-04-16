import java.util.*;

public class JeuJoueur {
    private static final int essaisMax = 9;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void joueurDevine(Scanner scanner, OuvrirDB db) {
        ArrayList<String> listMot = new ArrayList<>();
        Random random = new Random();

        int tailleMots = random.nextInt(3)+6;   // get random length
        listMot = db.getAllPhrase().get(tailleMots).line;  // depuis la db, et choisis ceux de bonne taille
        String motSecret = listMot.get(random.nextInt(listMot.size())); // get random mot
        
        System.out.println("🫵 Vous devez deviner un mot de " + GRAS + motSecret.length() + " lettres en " + essaisMax + " tentatives" + RESET + ".");

        for (int essai = 1; essai <= essaisMax; essai++) {
            System.out.print("Essai " + essai + "/" + essaisMax + " : ");
            String proposition = scanner.nextLine().toLowerCase(); // prend l'input

            if (proposition.length() != motSecret.length()) {
                System.out.println("Le mot doit contenir " + motSecret.length() + " lettres.");
                essai--;
                // si ya un oupsie, ne pénalise pas l'utilisateur et le fait réésayer
                continue;
            }

            EtatMot.pprint(proposition, motSecret); // afficher la progression à m'utilisateur

            if (EtatMot.checkEtatMot(proposition, motSecret).equals(motSecret)) { // test victoire
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

    // un run de test
    public static void main(String[] args) {
        OuvrirDB db = new OuvrirDB(); // ouvrir les mots
        Scanner scanner = new Scanner(System.in); // le scanner pour l'input (pour tester)
        joueurDevine(scanner, db);
    }
}
