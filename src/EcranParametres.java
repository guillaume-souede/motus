import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class EcranParametres extends JFrame {
    private ParametresJeu parametresJeu;
    private static boolean commencerJeu = false;

    public static boolean isCommencerJeu() {
        return commencerJeu;
    }

    public EcranParametres() {
        setTitle("Paramètres");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        JLabel modeDeJeuLabel = new JLabel("Mode de Jeu");
        panel.add(modeDeJeuLabel);

        JRadioButton ordiButton = new JRadioButton("Ordinateur");
        JRadioButton joueurButton = new JRadioButton("Joueur");
        JRadioButton mortelButton = new JRadioButton("Mortel");

        ButtonGroup group = new ButtonGroup();
        group.add(ordiButton);
        group.add(joueurButton);
        group.add(mortelButton);

        JPanel ordiFrame = new JPanel();
        ordiFrame.setLayout(new GridLayout(1, 2));
        ordiFrame.add(ordiButton);

        JTextField motTextField = new JTextField("mot de 6 à 9 lettres", 15);
        motTextField.setEnabled(false);
        ordiFrame.add(motTextField);
        panel.add(ordiFrame);

        JPanel difficultyPanel = new JPanel();
        JLabel difficultyLabel = new JLabel("Difficulté :");
        String[] niveaux = {"Défaut", "Difficile", "Difficile+"};
        JComboBox<String> difficultySelector = new JComboBox<>(niveaux);
        difficultySelector.setEnabled(false);
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultySelector);
        panel.add(difficultyPanel);

        motTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (motTextField.getText().trim().equalsIgnoreCase("mot de 6 à 9 lettres")) {
                    motTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (motTextField.getText().trim().isEmpty()) {
                    motTextField.setText("mot de 6 à 9 lettres");
                }
            }
        });

        panel.add(joueurButton);
        panel.add(mortelButton);

        ordiButton.addActionListener(event -> {
            motTextField.setEnabled(true);
            motTextField.setText("mot de 6 à 9 lettres");
            difficultySelector.setEnabled(true);
        });

        joueurButton.addActionListener(event -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
            difficultySelector.setEnabled(false);
        });

        mortelButton.addActionListener(event -> {
            motTextField.setEnabled(false);
            motTextField.setText("mot de 6 à 9 lettres");
            difficultySelector.setEnabled(false);
        });

        JButton jouerButton = new JButton("JOUER !");
        panel.add(jouerButton);

        jouerButton.addActionListener(event -> {
            ParametresJeu.Mode mode;
            if (ordiButton.isSelected()) {
                String mot = motTextField.getText().trim();
                String difficulte = (String) difficultySelector.getSelectedItem();
                if (mot.matches("[a-zA-Z]{6,9}")) {
                    mode = ParametresJeu.Mode.ORDI;
                    parametresJeu = new ParametresJeu(mode, mot, difficulte);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Saisir entre 6 et 9 caractères non-spéciaux.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else if (joueurButton.isSelected()) {
                mode = ParametresJeu.Mode.JOUEUR;
                parametresJeu = new ParametresJeu(mode, null, null);
                dispose();
            } else if (mortelButton.isSelected()) {
                mode = ParametresJeu.Mode.MORTEL;
                parametresJeu = new ParametresJeu(mode, null, null);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un mode de jeu.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public ParametresJeu getParametresJeu() {
        return parametresJeu;
    }

    public static void main(String[] args) {
        new EcranParametres();
    }
}
