import java.util.*;
import javax.swing.*;

public class MainTemporary {
    public static void main(String[] args) {
        // Afficher l'écran de chargement
        EcranChargement.afficher();

        // Ouvrir l'écran des paramètres
        SwingUtilities.invokeLater(() -> {
            EcranParametres ecranParametres = new EcranParametres((mode, motSecret) -> {
                // Une fois les paramètres récupérés, ouvrir EcranJeu
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("Motus - Jeu");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800, 600);

                    // Créer et ajouter EcranJeu avec les paramètres
                    EcranJeu ecranJeu = new EcranJeu("images/source.png", motSecret);
                    frame.add(ecranJeu);

                    frame.setVisible(true);
                });
            });
            ecranParametres.setVisible(true);
        });
    }
}