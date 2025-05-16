import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OuvrirDB {

    private final HashMap<Integer, Mots> phrase; // int = taille du mot

    // Initialiser DB
    public OuvrirDB(String filePath) {
        phrase = new HashMap<>();
        createDict(filePath);
    }

    // Getter pour tous les mots
    public HashMap<Integer, Mots> getAllPhrase() {
        return phrase;
    }

    // Getter pour mots de taille voulue
    public ArrayList<String> getOnePhrase(int taille) {
        Mots mots = phrase.get(taille);
        return (mots != null) ? mots.getLine() : new ArrayList<>();
    }

    // Attraper un mot au hasard de taille voulue
    public String getRandomWord(int taille) {
        ArrayList<String> words = getOnePhrase(taille);
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("No words found for the specified length: " + taille);
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size())).toUpperCase();
    }

    // Faire un dico depuis le fichier
    public void createDict(String nomfichier) {
        try (BufferedReader buf = new BufferedReader(new FileReader(nomfichier))) {
            String contentLine = buf.readLine();
            while (contentLine != null) {
                int fooKey = contentLine.length();
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

    // Static method to read rules from a file
    public static String lireRegle(String filePath) {
        StringBuilder out = new StringBuilder();
        try (BufferedReader buf = new BufferedReader(new FileReader(filePath))) {
            String line = buf.readLine();
            while (line != null) {
                out.append(line).append('\n');
                line = buf.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}