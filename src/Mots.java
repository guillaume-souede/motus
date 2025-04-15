import java.util.ArrayList;

public class Mots {

    private int nChar;
    ArrayList<String> line;

    // Retourner la ligne
    public ArrayList<String> getLine() {
        return line;
    }
    // Retourner la taille du mot occupant la ligne
    public int getnChar() {
        return nChar;
    }

    // Liste de Mots
    public Mots(String line){
        this.line = new ArrayList<>();
        this.line.add(line);
    }

    // Agrandir la liste de Mots
    public void extend(String mot){
        line.add(mot);
    }

}