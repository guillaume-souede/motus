import java.util.ArrayList;

public class Mots {

    private final int nChar; // Length of the words
    private final ArrayList<String> line;

    // Initialiser liste : 1ier mot + nChar
    public Mots(String word) {
        this.line = new ArrayList<>();
        this.line.add(word);
        this.nChar = word.length();
    }

    // Getter de la liste
    public ArrayList<String> getLine() {
        return line;
    }

    // Getter du nombre de caractères
    public int getnChar() {
        return nChar;
    }

    // Ajout mot à liste
    public void extend(String word) {
        if (word.length() != nChar) {
            throw new IllegalArgumentException("Taille différente : " + nChar);
        }
        line.add(word);
    }
}