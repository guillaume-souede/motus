public class ParametresJeu {

    public enum Mode {
        ORDI, 
        JOUEUR, 
        MORTEL;
    }

    private Mode mode;
    private String mot;
    private String difficulte;

    public ParametresJeu(Mode mode, String mot, String difficulte) {
        this.mode = mode;
        this.mot = mot;
        this.difficulte = difficulte;
    }

    public Mode getMode() {
        return mode;
    }

    public String getMot() {
        return mot;
    }

    public String getDifficulte() {
        return difficulte;
    }
}
