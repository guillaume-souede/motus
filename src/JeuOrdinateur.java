import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class JeuOrdinateur {
    private static final int essaisMax = 7;
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
            String progressionMot = "", lettresImpossible = "";
            HashMap<Integer,Character> lettresConnu = new HashMap<>(); // hashmap car stocke et les chars et les
            // 1. get la situation actuelle du mot
            
            //a. get les positions justes
            progressionMot = EtatMot.checkEtatMot(proposition, motSecret);
            
            //b. get les chars mal placées
            lettresConnu = EtatMot.checkWrongPlacement2(proposition, motSecret);

            //c. lettres impossibles 
            lettresImpossible = EtatMot.extractImpossiblechar(proposition, motSecret);

            // 2. filtrage du dictionnaire avec les possible
            dictionnaire = LogiqueBot.choix(progressionMot, lettresConnu, lettresImpossible, dictionnaire);

            // 3. get un mot du dico et le supprime après
            proposition = dictionnaire.get(random.nextInt(dictionnaire.size()));
            while(dictionnaire.remove(proposition)) { } // https://stackoverflow.com/questions/13565876/remove-all-occurrences-of-an-element-from-arraylist
            essais++;

            // 4. test si juste ou non (majoritairement juste de l'affichage)

            System.out.println("Essai " + (essais) + " : L'ordinateur propose " + GRAS + proposition + RESET);
            EtatMot.pprint(proposition, motSecret);
            //a. si vrai
            if (proposition.equals(motSecret)) {
                System.out.println("🥳 ⓂⓄⓉⓊⓈ de l'ordinateur en " + (essais) + " essai(s) !");
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
    Random random = new Random();

    int tailleMots = random.nextInt(3)+6;   // get random length
    ArrayList<String> listMot = db.getAllPhrase().get(tailleMots).line;  // depuis la db, et choisis ceux de bonne taille
    String motSecret = listMot.get(random.nextInt(listMot.size())); // get random mot
    
    //ordinateurDevine("preferee", scanner , db);
    ordinateurDevine(motSecret, scanner , db);
}

}