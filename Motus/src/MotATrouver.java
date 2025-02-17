import java.util.Arrays;

class MotATrouver {

    private final String motSecret;
    private final char[] etatMot;

    public MotATrouver(String mot) {
        this.motSecret = mot;
        this.etatMot = new char[mot.length()];
        Arrays.fill(this.etatMot, '_');
    }

    public boolean verifierProposition(String proposition) {
        if (proposition.equals(motSecret)) {
            return true;
        }
        for (int i = 0; i < motSecret.length(); i++) {
            char lettreProposee = proposition.charAt(i);
            if (lettreProposee == motSecret.charAt(i)) {
                etatMot[i] = Character.toUpperCase(lettreProposee);
            } else if (motSecret.contains(String.valueOf(lettreProposee))) {
                etatMot[i] = Character.toLowerCase(lettreProposee);
            }
        }
        return false;
    }

    public char[] getEtatMot() {
        return etatMot;
    }
}