import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Bienvenue dans ⓂⓄⓉⓊⓈ !");
            System.out.println("Choisissez un mode : 1️⃣(🫵 vous devinez) ou 2️⃣ (🤖 l'ordinateur devine)");
            int mode = scanner.nextInt();
            scanner.nextLine();

            if (mode == 1) {
                JeuJoueur.joueurDevine(scanner);
            } else if (mode == 2) {
                System.out.println("Entrez un mot secret pour l'ordinateur : ");
                String motSecret = scanner.nextLine();
                JeuOrdinateur.ordinateurDevine(motSecret, scanner);
            } else {
                System.out.println("Mode invalide.");
            }

            System.out.println("⏯️ Jouer à nouveau ? 1️⃣ : oui, 2️⃣ : non");
        } while (scanner.nextLine().equalsIgnoreCase("1"));

        scanner.close();
        System.out.println("👋 Au revoir.");
    }
}
