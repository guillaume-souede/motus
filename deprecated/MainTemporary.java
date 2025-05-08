import javax.swing.SwingUtilities;

public class MainTemporary {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcranParametres ecranParametres = new EcranParametres();

            ecranParametres.setVisible(true);

            // Thread : jsp pk mais sans ça marche pas et la fenêtre est vide
            // toujours chez Doudoux' : https://www.jmdoudoux.fr/java/dej/chap-threads.htm
            new Thread(() -> {
                while (ecranParametres.isVisible()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ParametresJeu modeChoisi = ecranParametres.getParametresJeu();
                if (modeChoisi == null) {
                    System.exit(0);
                }

                Runnable chargementFini = () -> {
                    String[] listePropositions = {"MOTUS", "AMOUROUX", "SOUEDE"};
                    EcranJeu jeu = new EcranJeu("images/apImage2.png", listePropositions);
                    jeu.setVisible(true);
                };

                SwingUtilities.invokeLater(() -> {
                    EcranChargement chargement = new EcranChargement(chargementFini);
                    chargement.setVisible(true);
                });
            }).start();
        });
    }
}
