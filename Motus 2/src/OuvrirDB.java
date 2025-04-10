import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public final class OuvrirDB {

    private final HashMap<Integer,Mots> phrase; // int = taille du mot

    // HashMap : clé = taille du mot ; valeur = le mot
    public HashMap<Integer, Mots> getAllPhrase() {
        return phrase;
    }

    public ArrayList<String> getOnePhrase(int taille){
        return phrase.get(taille).line;
    }


    public OuvrirDB() {
        phrase = new HashMap<>();
        this.initialisationDB();
    }


    public void createDict(String nomfichier) {
        // Ouverture du fichier (création du BufferReader)
        // Stockage des mots dans le HashMap :
        // Clé = taille du mot ; Valeur = le mot
        try (BufferedReader buf = new BufferedReader(new FileReader(nomfichier))) {
            // Lecture ligne par ligne
            String contentLine = buf.readLine();
            while (contentLine != null) {
                // Récupérer la taille de la ligne
                int fooKey = contentLine.length();
                // 
                if (phrase.get(fooKey) == null) {
                    phrase.put(fooKey, new Mots(contentLine));
                } else {
                    phrase.get(fooKey).extend(contentLine);
                }
                contentLine = buf.readLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur sur le fichier : " + e.getMessage());
        }
    }


    public void initialisationDB(){
        ArrayList<String> cheminDB = new ArrayList<>();
        // Ajout du fichier de mots
        cheminDB.add("data/motsMotus.txt");

        // Ouverture et Stockage des données/mots
        for (String string : cheminDB) {
            this.createDict(string);
        }
    }

}