import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

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
    private ArrayList<String> propositions = new ArrayList<>();
    private GrilleMotusPanel grillePanel;
    private final JTextField inputField = new JTextField(10);
    private final JButton validerBtn = new JButton("Valider");
    private final JLabel progressionLabel = new JLabel("/"); // Par défaut, 0/6
    private boolean jeuTermine = false;
    private Random random = new Random();

    public EcranJeu(String bgPath) {
        super("Motus");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Taille fenêtre
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                setTitle("Motus (" + width + "x" + height + ")");
            }
        });

        // Grille et fond
        int lignes = random.nextInt(3)+6; // Par défaut
        int colonnes = random.nextInt(3)+6; // Par défaut
        this.grillePanel = new GrilleMotusPanel(lignes, colonnes, bgPath);
        essaisMax = lignes; // Le nombre d'essais max est égal au nombre de lignes

        JScrollPane scrollPane = new JScrollPane(grillePanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        //
        JPanel inputPanel = new JPanel(new BorderLayout());

        // BoutonsG
        JPanel leftPanel = new JPanel();
        JComboBox<Integer> tailleComboBox = new JComboBox<>(new Integer[]{6, 7, 8, 9});
        tailleComboBox.setSelectedIndex(0); // Valeur par défaut : 6
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"👨", "🤖"});
        modeComboBox.setSelectedIndex(0); // Valeur par défaut : 👨

        // Reset
        JButton resetBtn = new JButton("⟳");
        resetBtn.addActionListener(e -> {
            // configuration actuelle
            String mode = (String) modeComboBox.getSelectedItem();
            int taille = grillePanel.getColonnes();

            // MàJ mot secret
            mettreAJourMotSecret(mode, taille);

            // Reset propositions
            propositions.clear();
            grillePanel.majGrille(propositions, motSecret);

            // Reset de l'image de fond
            grillePanel.setBackgroundImage(currentBackgroundImage);

            // Reset des champs de saisie
            inputField.setEnabled(true);
            validerBtn.setEnabled(false);
            inputField.setText("");
            progressionLabel.setText("0/" + taille);
        });

        // Mot secret en fonction du mode
        modeComboBox.addActionListener(e -> {
            String mode = (String) modeComboBox.getSelectedItem();
            int taille = (int) tailleComboBox.getSelectedItem();
            mettreAJourMotSecret(mode, taille);

            // Champs activés selon mode
            inputField.setEnabled(true);
            validerBtn.setEnabled(true);
        });

        // ActionListener pour JComboBox "Taille"
        tailleComboBox.addActionListener(e -> {
            String mode = (String) modeComboBox.getSelectedItem();
            int taille = (int) tailleComboBox.getSelectedItem();

            // Reset jeu
            mettreAJourMotSecret(mode, taille);
            propositions.clear();
            grillePanel.setColonnes(taille);
            grillePanel.majGrille(propositions, motSecret);

            // Reset champs
            inputField.setEnabled(true);
            validerBtn.setEnabled(false);
            inputField.setText("");
            progressionLabel.setText("0/" + taille);
        });

        // ComposantsG
        leftPanel.add(new JLabel("Taille:"));
        leftPanel.add(tailleComboBox);
        leftPanel.add(new JLabel("Mode:"));
        leftPanel.add(modeComboBox);
        leftPanel.add(resetBtn); // Ajouter le bouton reset

        // BoutonsD
        JPanel rightPanel = new JPanel();
        JButton optionsBtn = new JButton("⚙");

        JPopupMenu popupMenu = new JPopupMenu();

        // ROUE DES PARAMETRES
        // Sous-menu "Essais"
        JMenu essaisMenu = new JMenu("Essais");
        ButtonGroup essaisGroup = new ButtonGroup();
        for (int i = 1; i <= 6; i++) {
            JRadioButtonMenuItem essaisOption = new JRadioButtonMenuItem(i + "");
            int essaisValue = i;
            essaisOption.addActionListener(e -> {
                grillePanel.setLignes(essaisValue);
                essaisMax = essaisValue; // MàJ nb d'essais max
                propositions.clear();
                grillePanel.majGrille(propositions, motSecret);
            });
            essaisGroup.add(essaisOption);
            essaisMenu.add(essaisOption);
        }

        // Sous-menu "Taille"
        JMenu tailleMenu = new JMenu("Taille");
        ButtonGroup tailleGroup = new ButtonGroup();
        for (int i = 6; i <= 9; i++) {
            JRadioButtonMenuItem tailleOption = new JRadioButtonMenuItem(i + "");
            int tailleValue = i;
            tailleOption.addActionListener(e -> {
                grillePanel.setColonnes(tailleValue);
                propositions.clear();
                grillePanel.majGrille(propositions, motSecret);
            });
            tailleGroup.add(tailleOption);
            tailleMenu.add(tailleOption);
        }

        // Sous-menu "Mode"
        JMenu modeMenu = new JMenu("Mode");
        ButtonGroup modeGroup = new ButtonGroup();

        JRadioButtonMenuItem humainOption = new JRadioButtonMenuItem("👨");
        humainOption.setSelected(true); //
        humainOption.addActionListener(e -> {
            mettreAJourMotSecret("👨", grillePanel.getColonnes());
        });
        modeGroup.add(humainOption);
        modeMenu.add(humainOption);

        JRadioButtonMenuItem robotOption = new JRadioButtonMenuItem("🤖");
        robotOption.addActionListener(e -> {
            mettreAJourMotSecret("🤖", grillePanel.getColonnes());
        });
        modeGroup.add(robotOption);
        modeMenu.add(robotOption);

        popupMenu.add(modeMenu);

        // Sous-menu "Thème"
        JMenu themeMenu = new JMenu("Thème");
        ButtonGroup themeGroup = new ButtonGroup();

        // Thème "Hiver"
        JRadioButtonMenuItem hiverOption = new JRadioButtonMenuItem("Hiver");
        hiverOption.addActionListener(e -> {
            currentBackgroundImage = "images/hiver.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });
        themeGroup.add(hiverOption);
        themeMenu.add(hiverOption);
        
        // Thème "Défaut"
        JRadioButtonMenuItem defautOption = new JRadioButtonMenuItem("Défaut");
        defautOption.addActionListener(e -> {
            currentBackgroundImage = "images/defaut.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });
        themeGroup.add(defautOption);
        themeMenu.add(defautOption);

        // Mode "Tutoriel"
        JMenuItem tutorielItem = new JMenuItem("Tutoriel");
        tutorielItem.addActionListener(e -> {
            // MàJ fond (comme un Thème)
            grillePanel.setBackgroundImage("images/tutoriel.png");

            // 2 ESSAIS
            grillePanel.setLignes(2);
            essaisMax = 2;

            // 6 LETTRES
            grillePanel.setColonnes(6);
            propositions.clear();
            grillePanel.majGrille(propositions, motSecret);

            // MàJ champs
            progressionLabel.setText("0/6");
            inputField.setEnabled(true);
            validerBtn.setEnabled(false);
            inputField.setText("");
        });
        popupMenu.add(tutorielItem);

        // Option "Reset"
        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(e -> {
            // Reset le jeu
            String mode = "👨";
            int taille = 6;
            mettreAJourMotSecret(mode, taille);

            // Reset grille
            propositions.clear();
            grillePanel.setColonnes(taille);
            grillePanel.setLignes(taille);
            grillePanel.majGrille(propositions, motSecret);

            // Réinitialise l'image de fond
            grillePanel.setBackgroundImage(currentBackgroundImage);

            // Reset champs
            progressionLabel.setText("0/" + taille);
            inputField.setEnabled(true);
            validerBtn.setEnabled(false);
            inputField.setText("");
        });

        // Option "Quitter"
        JMenuItem quitterItem = new JMenuItem("Quitter");
        quitterItem.addActionListener(e -> System.exit(0));

        // SOUS-MENU (ORDRE INVERSé !!)
        popupMenu.add(themeMenu);
        popupMenu.add(essaisMenu);
//        popupMenu.add(tailleMenu);
//        popupMenu.add(modeMenu);
        popupMenu.add(tutorielItem);
//        popupMenu.add(resetItem);
        popupMenu.add(quitterItem);

        // survol bouton (écouter l' ampoule)
        // 💡🙏
        optionsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                popupMenu.show(optionsBtn, optionsBtn.getWidth() / 2, -popupMenu.getPreferredSize().height);
            }
        });
        this.grillePanel = new GrilleMotusPanel(lignes,colonnes,bgPath);

        rightPanel.add(optionsBtn);

        // Footer
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Proposition :"));
        centerPanel.add(inputField);
        centerPanel.add(progressionLabel);
        centerPanel.add(validerBtn);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Progrès mot
                int currentLength = inputField.getText().length();
                int targetLength = motSecret.length();

                // MàJ
                progressionLabel.setText(currentLength + "/" + targetLength);

                // Couleurs
                // Si mot OK
                if (currentLength == targetLength) {
                    progressionLabel.setForeground(Color.GREEN);
                    validerBtn.setEnabled(true);
                // Sinon
                } else {
                    progressionLabel.setForeground(Color.RED);
                    validerBtn.setEnabled(false);
                }
            }
        });

        inputPanel.add(leftPanel, BorderLayout.WEST);
        inputPanel.add(centerPanel, BorderLayout.CENTER);
        inputPanel.add(rightPanel, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        validerBtn.addActionListener(e -> traiterProposition());
        inputField.addActionListener(e -> traiterProposition());

        // Changer taille selon l'écran ?
        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialiser le mot secret par défaut
        modeComboBox.setSelectedIndex(0);
        validerBtn.setEnabled(false); // Désactiver le bouton "Valider" au démarrage
    }

    // partie traitenment du jeu
    private void traiterProposition() {
        String prop = inputField.getText().trim().toUpperCase();

        if (prop.length() != motSecret.length()) {
            return;
        }

        if (propositions.size() >= essaisMax){ 
            return;
        }

        propositions.add(prop);

        // met a jour l'IG
        grillePanel.majGrille(propositions, motSecret);

        if (prop.equals(motSecret)) {
            JOptionPane.showMessageDialog(this, "Bravo ! Motus trouvé en " + propositions.size() + " essais.");
            grillePanel.setBackgroundImage("images/victoire.png");
            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
            jeuTermine = true; // Jeu terminé après une victoire

        } else if (propositions.size() == essaisMax) {
            JOptionPane.showMessageDialog(this, "Perdu ! Le mot était : " + motSecret);
            grillePanel.setBackgroundImage("images/defaite.png");
            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
            jeuTermine = true; // Jeu terminé après une défaite

        } else {
            validerBtn.setEnabled(false);
        }
        inputField.setText("");
    }

    private void mettreAJourMotSecret(String mode, int taille) {
        if ("👨".equals(mode)) {
            // Mode 👨
            try {
                OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
                motSecret = db.getRandomWord(taille);
            } catch (Exception e) {
                e.printStackTrace();
                }
        } else if ("🤖".equals(mode)) {
            // Mode 🤖
            String propostion = "";
            JoueurProposeMot frameHumainPropose = new JoueurProposeMot();
            motSecret = frameHumainPropose.getMot();
        }
    }

    public static void main(String[] args) {
        String bgPath = "images/defaut.png";
        SwingUtilities.invokeLater(() -> new EcranJeu(bgPath));
    }
}