import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

// cf. https://www.jmdoudoux.fr/java/dej/chap-swing.htm
// RADIO BUTTONS FOR PC OR PLAYER MODE.
// JTextField FOR THE WORD TO FIND (PC MODE ONLY).
// BUTTON TO START THE GAME.

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
        JRadioButton mortelButton = new JRadioButton("Mortel");

        ButtonGroup group = new ButtonGroup();
        group.add(ordiButton);
        group.add(joueurButton);
        group.add(mortelButton);

        ordiButton.setToolTipText("Faites deviner un mot !");
        joueurButton.setToolTipText("Devinez un mot !");
        mortelButton.setToolTipText("Ouvrez un terminal et lancez la commande 'sudo rm -rf /'");

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Ordinateur
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ordiButton, gbc);

        JTextField motTextField = new JTextField("mot de 6 à 9 lettres", 15);
        motTextField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(motTextField, gbc);

        // FocusListener 
        motTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
            if (motTextField.getText().trim().equalsIgnoreCase("mot de 6 à 9 lettres")) {
                motTextField.setText("");
            }
            }
        });

        // Joueur
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(joueurButton, gbc);

        // Mortel
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(mortelButton, gbc);

        // FAIRE UNE CLASSE POUR EVITER LA REPETITION ? : 
        ordiButton.addActionListener(_ -> {
            motTextField.setEnabled(true);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        joueurButton.addActionListener(_ -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        mortelButton.addActionListener(_ -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        // "JOUER" button
        JButton jouerButton = new JButton("JOUER");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(jouerButton, gbc);

        // ActionListener
        // GLOIRE A L'AMPOULE (code corrigé)
        jouerButton.addActionListener(_ -> {
            if (ordiButton.isSelected()) {
                String mot = motTextField.getText();
                JOptionPane.showMessageDialog(f, "Mode Ordinateur. Mot : " + mot);
            } else if (joueurButton.isSelected()) {
                JOptionPane.showMessageDialog(f, "Mode Joueur.");
            } else if (mortelButton.isSelected()) {
                JOptionPane.showMessageDialog(f, "Au revoir. \n - VGE, 1981");
            } else {
                JOptionPane.showMessageDialog(f, "Sélectionnez un mode de jeu.");
            }
        });

        f.getContentPane().add(panel);
        f.setVisible(true);
    }
}