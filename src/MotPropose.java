public class MotPropose {
    private String proposition;
    private char[] etatMot;

    public MotPropose(String proposition, char[] etatMot) {
        this.proposition = proposition;
        this.etatMot = etatMot;
    }

    public String getProposition() {
        return proposition;
    }

    public char[] getEtatMot() {
        return etatMot;
    }
}
