import javax.swing.*;
import java.awt.*;

public class EcranChargement extends JFrame {
    private JProgressBar barreChargement;
    private int progress = 0;

    public EcranChargement() {
        setTitle("Chargement");
        setSize(600, 400);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel background = new JLabel(new ImageIcon("images/bec.jpg"));
        background.setLayout(new BorderLayout());

        barreChargement = new JProgressBar();
        barreChargement.setStringPainted(true);
        barreChargement.setForeground(Color.GREEN);
        barreChargement.setValue(0);

        background.add(barreChargement, BorderLayout.SOUTH);
        add(background);

        Timer timer = new Timer(50, e -> updateProgress((Timer) e.getSource()));
        timer.start();
    }

    private void updateProgress(Timer timer) {
        if (progress < 100) {
            progress += 2;
            barreChargement.setValue(progress);
        } else {
            timer.stop();
            dispose();
            Main.startGame();
        }
    }
}
