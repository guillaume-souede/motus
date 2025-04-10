import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OuvrirDB db = new OuvrirDB(); // ici on a l'ouverture + création de la DB

        do {
            System.out.println("Bienvenue dans ⓂⓄⓉⓊⓈ !");
            System.out.println("Choisissez un mode : 1️⃣(🫵 vous devinez) ou 2️⃣ (🤖 l'ordinateur devine)");
            int mode = scanner.nextInt();
            scanner.nextLine();

            if (mode == 1) {
                JeuJoueur.joueurDevine(scanner, db);
            } else if (mode == 2) {
                String motSecret;
                do {
                    System.out.println("Entrez un mot secret pour l'ordinateur (6, 7, 8 ou 9 lettres) : ");
                    motSecret = scanner.nextLine();
                    if (motSecret.length() < 6 || motSecret.length() > 9) {
                        System.out.println("❌ Mot invalide. Le mot doit contenir 6, 7, 8 ou 9 lettres.");
                    }
                } while (motSecret.length() < 6 || motSecret.length() > 9);

                JeuOrdinateur.ordinateurDevine(motSecret, scanner, db);
            } else {
                System.out.println("Mode invalide.");
            }

            System.out.println("⏯️ Jouer à nouveau ? 1️⃣ : oui, 2️⃣ : non");
        } while (scanner.nextLine().equalsIgnoreCase("1"));

        scanner.close();
        System.out.println("👋 Au revoir.");
    }
}