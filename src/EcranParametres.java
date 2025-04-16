import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

public class EcranParametres extends JFrame {
    private ParametresJeu parametresJeu; // params du joueur MAIS ajouter ceux du bot ? plus de customisation ?
    private static boolean commencerJeu = false;

    public static boolean isCommencerJeu() {
        return commencerJeu;
    }

    public EcranParametres() {
        setTitle("Paramètres");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

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

        motTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (motTextField.getText().trim().equalsIgnoreCase("mot de 6 à 9 lettres")) {
                    motTextField.setText("");
                }
            }
        });

        panel.add(joueurButton);
        panel.add(mortelButton);

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

        JButton jouerButton = new JButton("JOUER !");
        panel.add(jouerButton);

        jouerButton.addActionListener(event -> {
            ParametresJeu.Mode mode;
            if (ordiButton.isSelected()) {
                String mot = motTextField.getText().trim();
    
                if (mot.matches("[a-zA-Z]{6,9}")) {
                    mode = ParametresJeu.Mode.ORDI;
                    parametresJeu = new ParametresJeu(mode, mot);
                    dispose(); // Fermer la fenêtre après validation
                } else {
                    JOptionPane.showMessageDialog(this, "Saisir entre 6 et 9 caractères non-spéciaux.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else if (joueurButton.isSelected()) {
                mode = ParametresJeu.Mode.JOUEUR;
                parametresJeu = new ParametresJeu(mode, null);
                dispose(); // Fermer la fenêtre après validation
            } else if (mortelButton.isSelected()) {
                mode = ParametresJeu.Mode.MORTEL;
                parametresJeu = new ParametresJeu(mode, null);
                dispose(); // Fermer la fenêtre après validation
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un mode de jeu.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        getContentPane().add(panel);
    }

    public ParametresJeu getParametresJeu() {
        return parametresJeu;
    }

    public static void main(String[] args) {
        EcranParametres ecran = new EcranParametres();
        ecran.setVisible(true);

        // sans ça les param ne sont pas récupérés
        while (ecran.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ParametresJeu parametres = ecran.getParametresJeu();
        if (parametres != null) {
            commencerJeu = true;
            System.out.println("Mode " + parametres.getMode());
            System.out.println("Mot " + parametres.getMot());
        } else {
            commencerJeu = false;
            System.out.println("Ràs.");
        }
    }
}