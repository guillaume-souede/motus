import javax.swing.SwingUtilities;

public class MainTemporary {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // When chargement is done, show parametres
            Runnable chargementFini = () -> {
                EcranParametres.main(null);
            };

            EcranChargement chargement = new EcranChargement(chargementFini);
            chargement.setVisible(true);
        });
    }
}
