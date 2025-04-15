import java.awt.*;
import javax.swing.*;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon originalIcon = new ImageIcon("images/chargementImage.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel background = new JLabel(scaledIcon);
        background.setLayout(new BorderLayout());

        barreChargement = new JProgressBar();
        barreChargement.setStringPainted(true);
        barreChargement.setForeground(Color.GREEN);
        barreChargement.setBackground(Color.DARK_GRAY);
        barreChargement.setFont(new Font("Arial", Font.BOLD, 14));
        barreChargement.setValue(0);

        background.add(barreChargement, BorderLayout.SOUTH);
        add(background);

        Timer timer = new Timer(50, e -> updateProgress((Timer) e.getSource()));
        timer.start();
    }

    private void updateProgress(Timer timer) {
        if (progress < 100) {
            progress += 3;
            barreChargement.setValue(progress);
        } else {
            timer.stop();
            dispose();
            if (onFinished != null) {
                onFinished.run(); // Trigger the next step
            }
        }
    }

    public void afficher() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
