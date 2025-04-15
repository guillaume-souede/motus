import java.util.*;

public class JeuOrdinateur {
    private static final int essaisMax = 32;
    private static final String GRAS = "\033[1m";
    private static final String RESET = "\033[0m";

    public static void ordinateurDevine(String motSecret, Scanner scanner, OuvrirDB db) {

        int essais = 0; boolean trying = true;
        ArrayList<String> dictionnaire = db.getOnePhrase(motSecret.length()); // prend tout les mots de taille a rechercher
        
        // Set<String> motsEssayes = new HashSet<>(); ==> normalement on peut juste enlever direct dans la BD de mot au fir et a meusure
        Random random = new Random();

        String proposition = motSecret.charAt(0)+"";     

        if (dictionnaire.isEmpty()) {
            System.out.println("ℹ Mot impossible à jouer.");
            return;
        }


        while (essais < essaisMax && !dictionnaire.isEmpty() && trying) {
            /*
             * progressionMot = les positions connues justes
             * lettresConnu = les lettres mal placées connues 
             * lettresImpossible = les lettres abscente dans le mot a trouver
             */
            String progressionMot = "", lettresConnu = "", lettresImpossible = "";
            
            // 1. get la situation actuelle du mot
            
            //a. get les positions justes
            progressionMot = EtatMot.checkEtatMot(proposition, motSecret);
            
            //b. get les chars mal placées
            lettresConnu = EtatMot.checkWrongPlacement(proposition, motSecret);

            //c. lettres impossibles 
            lettresImpossible = EtatMot.extractImpossiblechar(proposition, motSecret);

            // 2. filtrage du dictionnaire avec les possible
            dictionnaire = LogiqueBot.choix(progressionMot, lettresConnu, lettresImpossible, dictionnaire);

            // 3. get un mot du dico et le supprime après
            proposition = dictionnaire.get(random.nextInt(dictionnaire.size()));
            dictionnaire.remove(proposition);
            essais++;

            // 4. test si juste ou non (majoritairement juste de l'affichage)

            System.out.println("Essai " + (essais + 1) + " : L'ordinateur propose " + GRAS + proposition + RESET);
            EtatMot.pprint(proposition, motSecret);
            //a. si vrai
            if (proposition.equals(motSecret)) {
                System.out.println("🥳 ⓂⓄⓉⓊⓈ de l'ordinateur en " + (essais + 1) + " essai(s) !");
                trying = false;
            }
            //b. si faux (rien de plus)
            
        }
        if (trying == true) {
            System.out.println("😢 Défaite après " + essaisMax + " essais.");
        }
    }

public static void main(String[] args) {
    OuvrirDB db = new OuvrirDB();
    Scanner scanner = new Scanner(System.in);

    ordinateurDevine("patate", scanner , db);
}

}