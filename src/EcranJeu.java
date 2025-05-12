import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EcranJeu extends JFrame {
    private int essaisMax;
    private String motSecret;
    private String currentBackgroundImage = "images/defaut.png";
    private final ArrayList<String> propositions = new ArrayList<>();
    private final GrilleMotusPanel grillePanel;
    private final JTextField inputField = new JTextField(10);
    private final JButton validerBtn = new JButton("Valider");
    private final JLabel progressionLabel = new JLabel("/");
    private boolean jeuTermine = false;
    protected String mode;

    // variables pour le mode IA
    protected String progVraie;
    HashMap<Integer,Character> charsMalPlace = new HashMap<>();
    String charImpossible;
    ArrayList<String> dicoMots = new ArrayList<>();

    public EcranJeu(String bgPath) {
        super("Motus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth(), height = getHeight();
                setTitle("Motus (" + width + "x" + height + ")");
            }
        });

        grillePanel = new GrilleMotusPanel(6, 6, bgPath);
        essaisMax = 6;

        JScrollPane scrollPane = new JScrollPane(grillePanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());

        // Gauche
        JPanel leftPanel = new JPanel();
        JComboBox<Integer> tailleComboBox = new JComboBox<>(new Integer[]{6, 7, 8, 9});
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"👨", "🤖"});
        mode = modeComboBox.getSelectedItem()+"";
        JButton resetBtn = new JButton("⟳");

        leftPanel.add(new JLabel("Taille:"));
        leftPanel.add(tailleComboBox);
        leftPanel.add(new JLabel("Mode:"));
        leftPanel.add(modeComboBox);
        leftPanel.add(resetBtn);

        // Centre
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Proposition :"));
        centerPanel.add(inputField);
        centerPanel.add(progressionLabel);
        centerPanel.add(validerBtn);

        // Droite
        JPanel rightPanel = new JPanel();
        JButton optionsBtn = new JButton("⚙");
        JPopupMenu popupMenu = createPopupMenu();
        optionsBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                popupMenu.show(optionsBtn, optionsBtn.getWidth() / 2, -popupMenu.getPreferredSize().height);
            }
        });
        rightPanel.add(optionsBtn);

        inputPanel.add(leftPanel, BorderLayout.WEST);
        inputPanel.add(centerPanel, BorderLayout.CENTER);
        inputPanel.add(rightPanel, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Listeners ComboBox
        tailleComboBox.addActionListener(e -> {
            int taille = (int) tailleComboBox.getSelectedItem();
            grillePanel.setColonnes(taille);
            mettreAJourMotSecret((String) modeComboBox.getSelectedItem(), taille);
            resetChamp(taille);
        });

        modeComboBox.addActionListener(e -> {
            int taille = grillePanel.getColonnes();
            String selectedMode = (String) modeComboBox.getSelectedItem();
            mode = selectedMode; // à ajouter
            mettreAJourMotSecret(selectedMode, taille);

            if ("🤖".equals(selectedMode)) {
                inputField.setEnabled(true); // Activer le champ pour saisir le mot mystère
                validerBtn.setEnabled(false); // Désactiver le bouton jusqu'à ce qu'un mot valide soit saisi
                progressionLabel.setText("0/6-9"); // Indiquer la plage de longueur valide
                progressionLabel.setForeground(Color.RED); // Rouge par défaut
            } else {
                inputField.setEnabled(true); // Activer le champ pour le joueur
                validerBtn.setEnabled(false);
                progressionLabel.setText("0/" + taille); // Réinitialiser l'affichage de progression
                progressionLabel.setForeground(Color.RED); // Rouge par défaut
            }
        });

        resetBtn.addActionListener(e -> {
            int taille = grillePanel.getColonnes(); // Récupérer la taille actuelle
            mettreAJourMotSecret((String) modeComboBox.getSelectedItem(), taille); // Mettre à jour le mot secret
            resetChamp(taille); // Réinitialiser l'état du jeu
        });

        inputField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = inputField.getText().trim();
                int currentLength = text.length();

                if ("🤖".equals(mode)) {
                    // Activer le bouton "Valider" uniquement si la longueur est entre 6 et 9
                    boolean isValid = currentLength >= 6 && currentLength <= 9;
                    validerBtn.setEnabled(isValid);

                    // Mettre à jour l'affichage de progression
                    progressionLabel.setText(currentLength + "/6-9");
                    progressionLabel.setForeground(isValid ? Color.GREEN : Color.RED);
                } else {
                    // Mode joueur : Activer le bouton "Valider" uniquement si la longueur correspond au mot secret
                    boolean isValid = motSecret != null && currentLength == motSecret.length();
                    validerBtn.setEnabled(isValid);

                    // Mettre à jour l'affichage de progression
                    progressionLabel.setText(currentLength + "/" + (motSecret != null ? motSecret.length() : ""));
                    progressionLabel.setForeground(isValid ? Color.GREEN : Color.RED);
                }
            }
        });

        validerBtn.addActionListener(e -> {
            if ("🤖".equals(mode)) {
                String motMystere = inputField.getText().trim().toUpperCase();
                if (motMystere.length() < 6 || motMystere.length() > 9) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un mot entre 6 et 9 caractères.");
                    return;
                }
                motSecret = motMystere; // Définir le mot mystère pour le Bot
                traiterProposition(mode); // Lancer la logique du Bot
            } else {
                traiterProposition(mode); // Mode joueur
            }
        });

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        modeComboBox.setSelectedIndex(0);
        validerBtn.setEnabled(false);
    }

    private void resetChamp(int taille) {
        propositions.clear(); // Réinitialiser les propositions
        grillePanel.majGrille(propositions, motSecret); // Réinitialiser la grille
        grillePanel.setBackgroundImage(currentBackgroundImage); // Réinitialiser l'image de fond
        inputField.setEnabled(true); // Réactiver le champ de saisie
        validerBtn.setEnabled(false); // Désactiver le bouton valider
        inputField.setText(""); // Vider le champ de saisie
        progressionLabel.setText("0/" + taille); // Réinitialiser l'affichage de progression
        jeuTermine = false; // Réinitialiser l'état du jeu
    }

    private void traiterProposition(String mode) {
        if ("🤖".equals(mode)) {
            if (motSecret == null || motSecret.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un mot mystère pour le Bot.");
                return;
            }

            // Initialiser les variables pour le Bot
            progVraie = "*".repeat(motSecret.length());
            charsMalPlace.clear();
            charImpossible = "";
            OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
            dicoMots = new ArrayList<>(db.getOnePhrase(motSecret.length()));

            // Faire jouer le Bot
            int essais = 0;
            boolean motTrouve = false;
            while (essais < essaisMax && !motTrouve) {
                dicoMots = LogiqueBot.choix(progVraie, charsMalPlace, charImpossible, dicoMots);
                String proposition = LogiqueBot.randomWord(dicoMots);
                propositions.add(proposition);
                grillePanel.majGrille(propositions, motSecret);

                if (proposition.equals(motSecret)) {
                    JOptionPane.showMessageDialog(this, "Le Bot a trouvé le mot : " + motSecret + " en " + (essais + 1) + " essais !");
                    grillePanel.setBackgroundImage("images/victoire.png");
                    motTrouve = true;
                } else {
                    // Mettre à jour les indices pour le Bot
                    charsMalPlace = EtatMot.checkWrongPlacement2(motSecret, proposition);
                    charImpossible += EtatMot.getImpossibleChars(motSecret, proposition);
                    progVraie = EtatMot.updateProgVraie(motSecret, proposition);
                }

                essais++;
                try {
                    Thread.sleep(1000); // Pause pour simuler le temps de réflexion du Bot
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!motTrouve) {
                JOptionPane.showMessageDialog(this, "Le Bot n'a pas trouvé le mot. Le mot était : " + motSecret);
                grillePanel.setBackgroundImage("images/defaite.png");
            }

            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
        } else {
            // Mode joueur (inchangé)
            String prop = inputField.getText().trim().toUpperCase();
            if (prop.length() != motSecret.length() || propositions.size() >= essaisMax) return;

            propositions.add(prop);
            grillePanel.majGrille(propositions, motSecret);

            if (prop.equals(motSecret)) {
                JOptionPane.showMessageDialog(this, "Bravo ! Motus trouvé en " + propositions.size() + " essais.");
                grillePanel.setBackgroundImage("images/victoire.png");
            } else if (propositions.size() == essaisMax) {
                JOptionPane.showMessageDialog(this, "Perdu ! Le mot était : " + motSecret);
                grillePanel.setBackgroundImage("images/defaite.png");
            }

            inputField.setEnabled(!prop.equals(motSecret));
            validerBtn.setEnabled(false);
            inputField.setText("");
        }
    }

    private void mettreAJourMotSecret(String mode, int taille) {
        if ("👨".equals(mode)) {
            try {
                OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
                motSecret = db.getRandomWord(taille);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            motSecret = null; // Il sera défini après saisie de l'utilisateur
            inputField.setText("");
            validerBtn.setEnabled(false);
            progressionLabel.setText("0/6-9");
            progressionLabel.setForeground(Color.RED);
        }
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenu essaisMenu = new JMenu("Essais");
        ButtonGroup essaisGroup = new ButtonGroup();
        for (int i = 1; i <= 6; i++) {
            JRadioButtonMenuItem essaisItem = new JRadioButtonMenuItem(String.valueOf(i));
            int value = i;
            essaisItem.addActionListener(e -> {
                essaisMax = value;
                grillePanel.setLignes(value);
                propositions.clear();
                grillePanel.majGrille(propositions, motSecret);
            });
            essaisGroup.add(essaisItem);
            essaisMenu.add(essaisItem);
        }

        JMenu themeMenu = new JMenu("Thème");
        ButtonGroup themeGroup = new ButtonGroup();

        JRadioButtonMenuItem hiver = new JRadioButtonMenuItem("Hiver");
        hiver.addActionListener(e -> {
            currentBackgroundImage = "images/hiver.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        JRadioButtonMenuItem defaut = new JRadioButtonMenuItem("Défaut");
        defaut.addActionListener(e -> {
            currentBackgroundImage = "images/defaut.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        JRadioButtonMenuItem reel = new JRadioButtonMenuItem("reel");
        defaut.addActionListener(e -> {
            currentBackgroundImage = "images/reel.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        JRadioButtonMenuItem reelHiver = new JRadioButtonMenuItem("reelHiver");
        defaut.addActionListener(e -> {
            currentBackgroundImage = "images/reelHivert.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        themeGroup.add(hiver); themeGroup.add(defaut);
        themeMenu.add(hiver); themeMenu.add(defaut);
        themeGroup.add(reel); themeGroup.add(reelHiver);
        themeMenu.add(reel); themeMenu.add(reelHiver);

        JMenuItem tutorielItem = new JMenuItem("Tutoriel");
        tutorielItem.addActionListener(e -> {
            grillePanel.setLignes(2);
            essaisMax = 2;
            grillePanel.setColonnes(6);
            grillePanel.setBackgroundImage("images/tutoriel.png");
            resetChamp(6);
            try {
                new EcranRegle();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.addActionListener(e -> System.exit(0));

        menu.add(essaisMenu);
        menu.add(themeMenu);
        menu.add(tutorielItem);
        menu.add(quitter);
        return menu;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EcranJeu("images/defaut.png"));
    }
}
