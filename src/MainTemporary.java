import javax.swing.SwingUtilities;

public class MainTemporary {

    public static void main(String[] args) {
        ParametresJeu modeChoisi;

        SwingUtilities.invokeLater(() -> {
            Runnable chargementFini = () -> {
                
                modeChoisi = EcranParametres.main(null); //on suppose que EcranParametres renvoie le mode de jeu
                
            };

            EcranChargement chargement = new EcranChargement(chargementFini);
            chargement.setVisible(true);

            String[] listeproposition = {"MOTUS", "AMOUROUX", "SOUEDE"};
            EcranJeu jeu = new EcranJeu("images/apImage2.png", listeproposition);
        });
    }
}