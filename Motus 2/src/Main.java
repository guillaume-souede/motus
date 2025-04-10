import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            OuvrirDB db = new OuvrirDB(); // ici on a l'ouverture + création de la DB

            do {
                System.out.println("Bienvenue dans ⓂⓄⓉⓊⓈ !");
                System.out.println("Choisissez un mode : 1️⃣(🫵 vous devinez) ou 2️⃣ (🤖 l'ordinateur devine)");
                int mode = scanner.nextInt();
                scanner.nextLine();

            // Sélection du mode de jeu avec le switch (cas 1 ou 2)
            switch (mode) {
                case 1 -> 
                    // Cas 1 : Mode Joueur (contre Ordinateur)
                    JeuJoueur.joueurDevine(scanner, db);
                case 2 -> {
                    // Cas 2 : Mode Ordinateur (contre Joueur)
                    String motSecret;
                    do {
                        // Demande et Saisie du mot secret au Joueur
                        System.out.println("Entrez un mot secret pour l'ordinateur (6 à 9 lettres) : ");
                        motSecret = scanner.nextLine();
                        // Tester si longueur OK ?
                        if (motSecret.length() < 6 || motSecret.length() > 9) {
                            System.out.println("❌ Mot invalide. Taille nécessaire : 6 à 9 lettres.");
                        }
                    } while (motSecret.length() < 6 || motSecret.length() > 9);

                    // Début jeu ordinateur
                    JeuOrdinateur.ordinateurDevine(motSecret, scanner, db);
                }
                default -> 
                    // Cas "AUTRE" : si choix invalide.
                    System.out.println("Mode invalide.");
            }

            // Rejouer ?
            System.out.println("⏯️ Jouer à nouveau ? 1️⃣ : oui, 2️⃣ : non");
            // Si 1 oui, si autre chose non !
        } while (scanner.nextLine().equalsIgnoreCase("1"));
            System.out.println("👋 Au revoir.");
        }
    }
}