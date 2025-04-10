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
        // for (char c : mot.toCharArray()) {
        EtatMot.checkWrongPlacement(proposition, motSecret, etatMot, occurrencesTemp);
        // }
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

        EtatMot.checkEtatMot(proposition, motSecret, etatMot, occurrencesTemp);
    }

    public void afficherEtatMot(String proposition) {
        StringBuilder affichageProgres = new StringBuilder();
        for (int i = 0; i < etatMot.length; i++) {
            char lettre = etatMot[i];
            if (lettre == '_') {
                affichageProgres.append("*");
            } else if (Character.toLowerCase(proposition.charAt(i)) == Character.toLowerCase(motSecret.charAt(i))) {
                affichageProgres.append(Character.toUpperCase(lettre));
            } else {
                affichageProgres.append('*'); // peut être lettre
            }
        }
        System.out.println(affichageProgres + "\n");

        // Solution Emoji
        StringBuilder affichageEmoji = new StringBuilder();
        for (int i = 0; i < etatMot.length; i++) {
            char lettre = proposition.charAt(i);
            if (lettre != motSecret.charAt(i)) {
                affichageEmoji.append("🟥");
            } else if (motSecret.contains(String.valueOf(lettre))) {
                affichageEmoji.append("🟡");
            } else {
                affichageEmoji.append("🟦");
            }
        }
        System.out.println(affichageEmoji + "\n");
    }

}