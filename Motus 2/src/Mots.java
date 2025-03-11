import java.util.ArrayList;

public class Mots {

    private int nChar;
    ArrayList<String> line;


    public ArrayList<String> getLine() {
        return line;
    }

    public int getnChar() {
        return nChar;
    }

    public Mots(String line){
        this.line = new ArrayList<>();
        this.line.add(line);
    }

    public void extend(String mot){
        line.add(mot);
    }

}
