import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OuvrirDB {

    private HashMap<Integer,Mots> phrase; // int = taille du mot



    public HashMap<Integer, Mots> getPhrase() {
        return phrase;
    }


    public OuvrirDB() {
        phrase = new HashMap<>();
        this.initialisationDB();
    }


    public void createDict(String nomfichier){
            String contentLine;
        try{
            BufferedReader buf = new BufferedReader(new FileReader(nomfichier));
            contentLine = buf.readLine();
            while(contentLine != null){
                if (contentLine != null)  {
                    int fooKey = contentLine.length();
                    if (phrase.get(fooKey) == null) {
                        
                        phrase.put(fooKey, new Mots(contentLine));
                    }
                    else {
                        phrase.get(fooKey).extend(contentLine);
                    }
                    contentLine = buf.readLine();
                }
            }
            buf.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public void initialisationDB(){
        ArrayList<String> cheminDB = new ArrayList<>();
        // ajout des fichier
        cheminDB.add("data/motsMotus.txt");

        // ouverture et stockege des donénes
        for (String string : cheminDB) {
            this.createDict(string);
        }
    }

}