import java.awt.*;
import javax.swing.*;

public class EcranChargement extends JFrame {
    private JProgressBar barreChargement;
    private int progress = 0;

    public EcranChargement() {
        setTitle("Chargement");
        setSize(600, 400);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Image d'arrière-plan + mise à l'échelle pour afficher les 2 noms d'auteurs
        ImageIcon originalIcon = new ImageIcon("images/chargementImage.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel background = new JLabel(scaledIcon);
        background.setLayout(new BorderLayout());

        // Faire la barre de Chargement
        barreChargement = new JProgressBar();
        barreChargement.setStringPainted(true);
        barreChargement.setForeground(Color.GREEN); // 1ier plan = barre
        barreChargement.setBackground(Color.DARK_GRAY); // Arrière plan
        barreChargement.setFont(new Font("Arial", Font.BOLD, 14)); // Affichage du '%
        // Je n'ai pas réussi à changer la couleur !!!!!!
        barreChargement.setValue(0); // init à zéro

        background.add(barreChargement, BorderLayout.SOUTH);
        add(background);

        Timer timer = new Timer(50, e -> updateProgress((Timer) e.getSource()));
        timer.start();
    }

    // Méthode pour le % de la barre de chargement
    // C'est une barre artificielle alors elle dépend du timer
    private void updateProgress(Timer timer) {
        if (progress < 100) {
            progress += 3;
            barreChargement.setValue(progress);
        } else {
            timer.stop();
            dispose();
        }
    }

    // Affichage de l'écran de chargement
    public static void afficher() {
        SwingUtilities.invokeLater(() -> {
            EcranChargement ecran = new EcranChargement();
            ecran.setVisible(true);
        });
    }
}