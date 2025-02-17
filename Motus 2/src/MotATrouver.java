import java.util.*;

public class MotATrouver {
    private final String motSecret;
    private final char[] etatMot;
    private final Map<Character, Integer> occurrencesRestantes;

    public MotATrouver(String mot) {
        this.motSecret = mot;
        this.etatMot = new char[mot.length()];
        Arrays.fill(this.etatMot, '_');

        this.occurrencesRestantes = new HashMap<>();
        for (char c : mot.toCharArray()) {
            occurrencesRestantes.put(c, occurrencesRestantes.getOrDefault(c, 0) + 1);
        }
    }

    public boolean verifierProposition(String proposition) {
        if (proposition.equals(motSecret)) {
            return true;
        }

        Map<Character, Integer> occurrencesTemp = new HashMap<>(occurrencesRestantes);

        for (int i = 0; i < motSecret.length(); i++) {
            if (proposition.charAt(i) == motSecret.charAt(i)) {
                etatMot[i] = proposition.charAt(i);
                occurrencesTemp.put(proposition.charAt(i), occurrencesTemp.get(proposition.charAt(i)) - 1);
            } else {
                etatMot[i] = '_';
            }
        }

        for (int i = 0; i < motSecret.length(); i++) {
            char lettre = proposition.charAt(i);
            if (etatMot[i] == lettre) continue;

            if (occurrencesTemp.getOrDefault(lettre, 0) > 0) {
                etatMot[i] = Character.toLowerCase(lettre);
                occurrencesTemp.put(lettre, occurrencesTemp.get(lettre) - 1);
            }
        }

        return false;
    }

    public void afficherEtatMot(String proposition) {
        StringBuilder affichage = new StringBuilder();
        for (int i = 0; i < etatMot.length; i++) {
            char lettre = proposition.charAt(i);
            if (lettre == motSecret.charAt(i)) {
                affichage.append("🟥").append(lettre);
            } else if (motSecret.contains(String.valueOf(lettre))) {
                affichage.append("🟡").append(lettre);
            } else {
                affichage.append("🟦").append(lettre);
            }
        }
        System.out.println(affichage);
    }

    public char[] getEtatMot() {
        return etatMot;
    }
}
