import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class EcranChargement extends JFrame {
    private final JProgressBar barreChargement;
    private int progress = 0;
    private final Runnable onFinished;

    public EcranChargement(Runnable onFinished) {
        this.onFinished = onFinished;

        setTitle("Chargement");
        setSize(600, 400);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon originalIcon = new ImageIcon("images/defaut.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel background = new JLabel(scaledIcon);
        background.setLayout(new BorderLayout());

        barreChargement = new JProgressBar();
        barreChargement.setStringPainted(true);
        barreChargement.setForeground(Color.GREEN);
        barreChargement.setBackground(Color.DARK_GRAY);
        barreChargement.setFont(new Font("Arial", Font.BOLD, 22)); // police plus grande
        barreChargement.setPreferredSize(new java.awt.Dimension(600, 40)); // barre plus haute
        barreChargement.setValue(0);
        barreChargement.setOpaque(true);
        barreChargement.setBorderPainted(false);
        barreChargement.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected java.awt.Dimension getPreferredInnerHorizontal() {
                return new java.awt.Dimension(600, 40);
            }
        });

        background.add(barreChargement, BorderLayout.SOUTH);
        add(background);

        Timer timer = new Timer(50, e -> updateProgress((Timer) e.getSource()));
        timer.start();
        setVisible(true);
    }

    public EcranChargement() {

        setTitle("Chargement");
        setSize(600, 400);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon originalIcon = new ImageIcon("images/chargementImage.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel background = new JLabel(scaledIcon);
        background.setLayout(new BorderLayout());

        barreChargement = new JProgressBar();
        this.onFinished = null;
        barreChargement.setStringPainted(true);
        barreChargement.setForeground(Color.GREEN);
        barreChargement.setBackground(Color.DARK_GRAY);
        barreChargement.setFont(new Font("Arial", Font.BOLD, 22)); // police plus grande
        barreChargement.setPreferredSize(new java.awt.Dimension(600, 40)); // barre plus haute
        barreChargement.setValue(0);
        barreChargement.setOpaque(true);
        barreChargement.setBorderPainted(false);
        barreChargement.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected java.awt.Dimension getPreferredInnerHorizontal() {
                return new java.awt.Dimension(600, 40);
            }
        });

        background.add(barreChargement, BorderLayout.SOUTH);
        add(background);

        Timer timer = new Timer(50, e -> updateProgress((Timer) e.getSource()));
        timer.start();
        setVisible(true);
    }

    private void updateProgress(Timer timer) {
        if (progress < 100) {
            progress += 3;
            barreChargement.setValue(progress);
        } else {
            timer.stop();
            new EcranJeu("images/defaut.png");
            dispose();
            if (onFinished != null) {
                onFinished.run(); // Trigger the next step
            }
        }
    }

    public void afficher() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
//     public static void main(String[] args) {
//         new EcranChargement();
//     }
}
