import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;


// TODO : vérifier que la taille du mot est correcte (6 à 9)
// qqchose Listener... ?
// TODO : faire en sorte qu'on NE puisse PAS jouer si mot incorrect

public class EcranParametresC extends JFrame {
    private ParametresJeu parametresJeu; // Instance pour stocker les paramètres choisis

    public EcranParametresC() {
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
                mode = ParametresJeu.Mode.ORDI;
                String mot = motTextField.getText();
                parametresJeu = new ParametresJeu(mode, mot);
            } else if (joueurButton.isSelected()) {
                mode = ParametresJeu.Mode.JOUEUR;
                parametresJeu = new ParametresJeu(mode, null);
            } else if (mortelButton.isSelected()) {
                mode = ParametresJeu.Mode.MORTEL;
                parametresJeu = new ParametresJeu(mode, null);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez MdJ.");
                return;
            }
            dispose();
        });

        getContentPane().add(panel);
    }

    public ParametresJeu getParametresJeu() {
        return parametresJeu;
    }

    public static void main(String[] args) {
        EcranParametresC ecran = new EcranParametresC();
        ecran.setVisible(true);

        while (ecran.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ParametresJeu parametres = ecran.getParametresJeu();
        if (parametres != null) {
            System.out.println("Mode " + parametres.getMode());
            System.out.println("Mot " + parametres.getMot());
        } else {
            System.out.println("Ràs.");
        }
    }
}