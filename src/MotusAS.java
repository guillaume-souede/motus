import java.util.*;

class MotATrouver {
    private final String motSecret;
    private final char[] etatMot;

    public MotATrouver(String mot) {
        this.motSecret = mot;
        this.etatMot = new char[mot.length()];
        Arrays.fill(this.etatMot, '_');
    }

    public boolean verifierProposition(String proposition) {
        if (proposition.equals(motSecret)) {
            return true;
        }
        for (int i = 0; i < motSecret.length(); i++) {
            char lettreProposee = proposition.charAt(i);
            if (lettreProposee == motSecret.charAt(i)) {
                etatMot[i] = Character.toUpperCase(lettreProposee);
            } else if (motSecret.contains(String.valueOf(lettreProposee))) {
                etatMot[i] = Character.toLowerCase(lettreProposee);
            }
        }
        return false;
    }

    public char[] getEtatMot() {
        return etatMot;
    }
}

public class MotusAS {
    private static final int essaisMax = 6;  // Définir le nombre d'essais maximum globalement
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Bienvenue dans ⓂⓄⓉⓊⓈ !");
            System.out.println("Choisissez un mode : 1️⃣(🫵 vous devinez) ou 2️⃣ (🤖 l'ordinateur devine)");
            int mode = scanner.nextInt();
            scanner.nextLine();

            if (mode == 1) {
                joueurDevine(scanner);
            } else if (mode == 2) {
                ordinateurDevine(scanner);
            } else {
                System.out.println("Mode invalide.");
            }

            System.out.println("⏯️ Jouer à nouveau ? 1️⃣ : oui, 2️⃣ : non");
        } while (scanner.nextLine().equalsIgnoreCase("1"));

        scanner.close();
        System.out.println("👋 Au revoir.");
    }

    public static void joueurDevine(Scanner scanner) {
        String[] motsPossibles = getListeMotsPossibles();
        Random random = new Random();
        String motSecret = motsPossibles[random.nextInt(motsPossibles.length)];
        MotATrouver motATrouver = new MotATrouver(motSecret);

        System.out.println("🫵 Vous devez deviner un mot de " + GRAS + motSecret.length() + " lettres en " + essaisMax + " tentatives" + RESET + ".");

        for (int essai = 1; essai <= essaisMax; essai++) {
            System.out.println("Mot actuel : " + Arrays.toString(motATrouver.getEtatMot()));
            System.out.print("Essai " + essai + "/" + essaisMax + " : ");
            String proposition = scanner.nextLine().toLowerCase();

            if (proposition.length() != motSecret.length()) {
                System.out.println("Le mot doit contenir " + motSecret.length() + " lettres.");
                essai--;
                continue;
            }

            if (motATrouver.verifierProposition(proposition)) {
                System.out.println("🥳 Félicitations !" + GRAS + "ⓂⓄⓉⓊⓈ en " + essai + " essai(s) sur " + essaisMax + RESET + ".");
                return;
            }
        }
        System.out.println("😥 Perdu ! Le mot était : " + motSecret);
    }

    public static void ordinateurDevine(Scanner scanner) {
        String[] dictionnaire = getListeMotsPossibles();
        System.out.println("ℹ Liste des mots possibles : " + Arrays.toString(dictionnaire));
        
        // Demande à l'utilisateur de rentrer un mot secret
        String motSecret;
        while (true) {
            System.out.print("Entrez un mot secret pour que l'ordinateur le devine : ");
            motSecret = scanner.nextLine().toLowerCase();
            
            // Vérifie si le mot est dans la liste des mots possibles
            boolean motValide = false;
            for (String mot : dictionnaire) {
                if (mot.equals(motSecret)) {
                    motValide = true;
                    break;
                }
            }
            
            // Si le mot est valide, on sort de la boucle
            if (motValide) {
                break;
            } else {
                System.out.println("ℹ Saisir un mot valide.");
            }
        }

        List<String> motsPossibles = new ArrayList<>();
        for (String mot : dictionnaire) {
            if (mot.length() == motSecret.length()) {
                motsPossibles.add(mot);
            }
        }

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

            if (motATrouver.verifierProposition(proposition)) {
                System.out.println("🥳 ⓂⓄⓉⓊⓈ de l'ordinateur en " + (essais + 1) + " essai(s) !");
                return;
            }

            motsPossibles.removeIf(mot -> !correspondAEtat(mot, motATrouver.getEtatMot()));
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

    private static String[] getListeMotsPossibles() {
        return new String[]{"avion", "voiture", "maison", "ordinateur", "bureau", "table", "chaise", "fenetre"};
    }

}
