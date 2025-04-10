import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// cf. https://www.jmdoudoux.fr/java/dej/chap-swing.htm

public class EcranParametres extends JFrame {

    public static void main(String[] args) {
        JFrame f = new JFrame("Paramètres");
        f.setSize(400, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel modeDeJeuLabel = new JLabel("Mode de Jeu");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(modeDeJeuLabel, gbc);

        JRadioButton ordiButton = new JRadioButton("Ordinateur");
        JRadioButton joueurButton = new JRadioButton("Joueur");
        ButtonGroup group = new ButtonGroup();
        group.add(ordiButton);
        group.add(joueurButton);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Ordinateur
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ordiButton, gbc);

        JTextField motTextField = new JTextField(15);
        motTextField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(motTextField, gbc);

        // Joueur
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(joueurButton, gbc);

        ordiButton.addActionListener(e -> motTextField.setEnabled(true));
        joueurButton.addActionListener(e -> motTextField.setEnabled(false));

        // "JOUER" but'
        JButton jouerButton = new JButton("JOUER");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(jouerButton, gbc);

        jouerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordiButton.isSelected()) {
                    String mot = motTextField.getText();
                    JOptionPane.showMessageDialog(f, "Mode Ordinateur. Mot : " + mot);
                } else if (joueurButton.isSelected()) {
                    JOptionPane.showMessageDialog(f, "Mode Joueur.");
                } else {
                    JOptionPane.showMessageDialog(f, "Veuillez sélectionner un mode de jeu.");
                }
            }
        });

        f.getContentPane().add(panel);
        f.setVisible(true);
    }
}
