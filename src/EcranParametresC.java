import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

// cf. https://www.jmdoudoux.fr/java/dej/chap-swing.htm
// RADIO BUTTONS FOR PC OR PLAYER MODE.
// JTextField FOR THE WORD TO FIND (PC MODE ONLY).
// BUTTON TO START THE GAME.

public class EcranParametresC extends JFrame {

    public static void main(String[] args) {
        JFrame f = new JFrame("Paramètres");
        f.setSize(400, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        // https://bbclone.developpez.com/fr/java/tutoriels/uiswing/gridbaglayout/?page=page_3
        panel.setLayout(new GridLayout(5,1)); // les 3 lignes


        JLabel modeDeJeuLabel = new JLabel("Mode de Jeu");
        panel.add(modeDeJeuLabel);

        JRadioButton ordiButton = new JRadioButton("Ordinateur");
        JRadioButton joueurButton = new JRadioButton("Joueur");
        JRadioButton mortelButton = new JRadioButton("Mortel");

        // attribution les groupes des 3 boutons exclusifs
        ButtonGroup group = new ButtonGroup();
        group.add(ordiButton);
        group.add(joueurButton);
        group.add(mortelButton);

        ordiButton.setToolTipText("Faites deviner un mot !");
        joueurButton.setToolTipText("Devinez un mot !");
        mortelButton.setToolTipText("Ouvrez un terminal et lancez la commande 'sudo rm -rf /'");


        // Ordinateur
        JPanel ordiFrame = new JPanel();
        ordiFrame.setLayout(new GridLayout(1, 2)); // 2 colonnes : 1 pour la coche et un autre pour le texte

        ordiFrame.add(ordiButton);                 // ajout du bouton mode ordi

        JTextField motTextField = new JTextField("mot de 6 à 9 lettres", 15);
        motTextField.setEnabled(false);
        ordiFrame.add(motTextField);               // ajout du textField

        panel.add(ordiFrame);                      // ajout du sous groupe texte + coche

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
        panel.add(joueurButton);

        // Mortel
        panel.add(mortelButton);

        // FAIRE UNE CLASSE POUR EVITER LA REPETITION ? : 
        ordiButton.addActionListener(event -> {
            motTextField.setEnabled(true);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        joueurButton.addActionListener(event -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        mortelButton.addActionListener(event -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
        });

        // "JOUER" button
        JButton jouerButton = new JButton("JOUER");
        panel.add(jouerButton);

        // ActionListener
        // GLOIRE A L'AMPOULE (code corrigé)
        jouerButton.addActionListener(event -> {
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