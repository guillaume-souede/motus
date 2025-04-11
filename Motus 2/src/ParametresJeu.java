public class ParametresJeu {
    public enum Mode {
        ORDI, JOUEUR, MORTEL
    }
/* Il faut intégrer ces horreurs à EcranParametres ! */
    private final Mode mode;
    private final String mot;

    public ParametresJeu(Mode mode, String mot) {
        this.mode = mode;
        this.mot = mot;
    }

    public Mode getMode() {
        return mode;
    }

    public String getMot() {
        return mot;
    }
}
