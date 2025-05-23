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
import javax.swing.JCheckBoxMenuItem;
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
    protected String progVraie = "";
    HashMap<Integer,Character> charsMalPlace = new HashMap<>();
    String charImpossible;
    ArrayList<String> dicoMots = new ArrayList<>();

    private boolean tutorielActif = false;
    private JCheckBoxMenuItem tutorielMenuItem; // Référence à l'item Tutoriel

    @SuppressWarnings("unused")
    public EcranJeu(String bgPath) {
        super("Motus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
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
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"👨", "🤖"});
        mode = modeComboBox.getSelectedItem()+"";
        JButton resetBtn = new JButton("⟳");

        leftPanel.add(new JLabel("Mode"));
        leftPanel.add(modeComboBox);
        leftPanel.add(resetBtn);

        // Centre
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Proposition"));
        centerPanel.add(inputField);
        centerPanel.add(progressionLabel);
        centerPanel.add(validerBtn);

        // Droite
        JPanel rightPanel = new JPanel();
        JButton optionsBtn = new JButton("⚙");
        JPopupMenu popupMenu = createPopupMenu();
        optionsBtn.addMouseListener(new MouseAdapter() {
            @Override
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
        modeComboBox.addActionListener(e -> {
            int taille = 6; // Toujours reset à 6 colonnes par défaut
            String selectedMode = (String) modeComboBox.getSelectedItem();
            mode = selectedMode;
            mettreAJourMotSecret(selectedMode, taille);

            // Reset complet de la grille et du jeu
            grillePanel.setColonnes(taille);
            grillePanel.majGrille(new ArrayList<>(), null);
            propositions.clear();
            inputField.setText("");
            inputField.setEnabled(true);
            validerBtn.setEnabled(false);
            progressionLabel.setText("0/6-9");
            progressionLabel.setForeground(Color.RED);

            // Remettre le thème par défaut
            currentBackgroundImage = "images/defaut.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        resetBtn.addActionListener(e -> {
            int taille = grillePanel.getColonnes(); // Récupérer la taille actuelle
            mettreAJourMotSecret((String) modeComboBox.getSelectedItem(), taille); // Mettre à jour le mot secret
            resetChamp(); // Réinitialiser l'état du jeu
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
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
                    if (motSecret == null) {
                        // Première saisie : taille libre entre 6 et 9
                        boolean isValid = currentLength >= 6 && currentLength <= 9;
                        validerBtn.setEnabled(isValid);
                        progressionLabel.setText(currentLength + "/6-9");
                        progressionLabel.setForeground(isValid ? Color.GREEN : Color.RED);
                    } else {
                        // Mode joueur : Activer le bouton "Valider" uniquement si la longueur correspond au mot secret
                        boolean isValid = currentLength == motSecret.length();
                        validerBtn.setEnabled(isValid);

                        // Mettre à jour l'affichage de progression
                        progressionLabel.setText(currentLength + "/" + motSecret.length());
                        progressionLabel.setForeground(isValid ? Color.GREEN : Color.RED);
                    }
                }
            }
        });

        inputField.addActionListener(e -> validerBtn.doClick());

        validerBtn.addActionListener(e -> {
            if ("🤖".equals(mode)) {
                String motMystere = inputField.getText().trim().toUpperCase();

                motSecret = motMystere; // définir le mot mystère pour le bot

                // AJOUTER CETTE LIGNE POUR ADAPTER LA GRILLE
                grillePanel.setColonnes(motSecret.length());
                grillePanel.majGrille(new ArrayList<>(), motSecret);

                // initialiser les variables pour le bot
                progVraie = "";
                charsMalPlace = new HashMap<>();
                charImpossible = "";

                // vérifier si motSecret est bien initialisé
                if (motSecret == null || motSecret.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le mot mystère n'a pas été défini !");
                    return;
                }

                // initialiser les variables pour le bot d'après LogiqueBot.java
                progVraie = motSecret.charAt(0) + ""; // initialiser la première lettre
                for (int i = 1; i < motSecret.length()-1; i++) {
                    progVraie += "*"; // initialiser la progression avec des étoiles
                }
                charsMalPlace.clear(); // réinitialiser les caractères mal placés
                charImpossible = ""; // réinitialiser les caractères impossibles
                OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
                dicoMots = new ArrayList<>();
                dicoMots = db.getOnePhrase(motSecret.length()); // charger le dictionnaire pour le bot
                
                // faire jouer le bot d'après LogiqueBot.java
                int essais = 0;
                boolean motTrouve = false;

                while (essais < essaisMax && !motTrouve) {
                    // filtrer les mots possibles avec la méthode choix
                    dicoMots = LogiqueBot.choix(progVraie, charsMalPlace, charImpossible, dicoMots);
                    if (dicoMots.isEmpty()) {
                        // JOptionPane.showMessageDialog(this, "Le bot n'a plus de mots possibles !");
                        break;
                    }

                    // choisir un mot aléatoire parmi les mots possibles
                    String proposition = LogiqueBot.randomWord(dicoMots).toLowerCase();
                    propositions.add(proposition.toUpperCase());
                    grillePanel.majGrille(propositions, motSecret); // mettre à jour la grille

                    // vérifier si le mot proposé est correct
                    if (proposition.equals(motSecret.toLowerCase())) {
                        terminerJeu(true, "Motus en " + (essais + 1) + " essais !");
                        motTrouve = true;
                    } else {
                        // mettre à jour les indices pour le bot
                        charsMalPlace = EtatMot.checkWrongPlacement2(motSecret.toLowerCase(),proposition);
                        charImpossible += EtatMot.getImpossibleChars(motSecret.toLowerCase(),proposition);
                        progVraie = EtatMot.updateProgVraie(motSecret.toLowerCase(), proposition);
                    }

                    essais++;
                    // try {
                    //     Thread.sleep(1000); // pause pour simuler le temps de réflexion du bot
                    // } catch (InterruptedException e1) {
                    //     e1.printStackTrace();
                    // }
                }

                if (!motTrouve) {
                    if (!dicoMots.contains(motSecret.toLowerCase())) {
                        terminerJeu(false, "Mot impossible.");
                    } else {
                        terminerJeu(false, "Perdu ! Le mot était : " + motSecret);
                    }
                }
            } else {
                traiterProposition(mode); // Mode joueur
            }
        });

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        modeComboBox.setSelectedIndex(0);
        validerBtn.setEnabled(false);
        progressionLabel.setText("0/6-9"); //ok

        // Forcer le mode tutoriel au premier lancement
        SwingUtilities.invokeLater(() -> {
            if (tutorielMenuItem != null && !tutorielMenuItem.isSelected()) {
                tutorielMenuItem.doClick();
            }
        });
    }

    private void resetChamp() {
        propositions.clear();
        grillePanel.majGrille(propositions, motSecret);
        grillePanel.setBackgroundImage(currentBackgroundImage);
        inputField.setEnabled(true);
        validerBtn.setEnabled(false);
        inputField.setText("");
        progressionLabel.setText("0/6-9"); // <-- Toujours afficher 0/6-9 au reset
        jeuTermine = false;
    }

    private void traiterProposition(String mode) {
        if ("🤖".equals(mode)) {
            String motMystere = inputField.getText().trim().toUpperCase();
            if (motMystere.length() < 6 || motMystere.length() > 9) {
                progressionLabel.setText("Mot invalide !");
                progressionLabel.setForeground(Color.RED);
                return;
            }
            motSecret = motMystere; // Définir le mot mystère pour le Bot
            inputField.setEnabled(false); // Désactiver le champ de saisie
            validerBtn.setEnabled(false); // Désactiver le bouton valider


            // Faire jouer le Bot
            int essais = 0;
            boolean motTrouve = false;
            while (essais < essaisMax && !motTrouve) {
                dicoMots = LogiqueBot.choix(progVraie, charsMalPlace, charImpossible, dicoMots);
                String proposition = LogiqueBot.randomWord(dicoMots);
                propositions.add(proposition);
                grillePanel.majGrille(propositions, motSecret); // Mettre à jour la grille

                if (proposition.equals(motSecret)) {
                    grillePanel.setBackgroundImage("images/victoire.png");
                    progressionLabel.setText("Motus en " + (essais + 1) + " essais !");
                    progressionLabel.setForeground(Color.GREEN);
                    motTrouve = true;
                } else {
                    // Mettre à jour les indices pour le Bot
                    charsMalPlace = EtatMot.checkWrongPlacement2(motSecret, proposition);
                    charImpossible += EtatMot.getImpossibleChars(motSecret, proposition);
                    progVraie = EtatMot.updateProgVraie(motSecret, proposition);
                }

                essais++;
            }

            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
        } else {
            // Mode joueur
            String prop = inputField.getText().trim().toUpperCase();
            if (motSecret == null) {
                // Première proposition : définir la taille et tirer le mot secret
                OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
                motSecret = db.getRandomWord(prop.length());
                grillePanel.setColonnes(prop.length());
                progressionLabel.setText(prop.length() + "/6-9");
                progressionLabel.setForeground(Color.GREEN);
            }

            // Ensuite, vérifier la taille
            if (prop.length() != motSecret.length() || propositions.size() >= essaisMax) return;

            if (prop.equals(motSecret)) {
                propositions.add(prop);
                grillePanel.majGrille(propositions, motSecret);
                terminerJeu(true, "Motus en " + propositions.size() + " essais !");
                // Ne pas réactiver inputField après victoire
            } else {
                propositions.add(prop);
                grillePanel.majGrille(propositions, motSecret);
                if (propositions.size() == essaisMax) {
                    terminerJeu(false, "Perdu ! Le mot était : " + motSecret);
                    return;
                }
            }

            // Ne réactiver inputField que si le jeu n'est pas terminé
            if (!jeuTermine) {
                inputField.setEnabled(true);
                validerBtn.setEnabled(false);
                inputField.setText("");
            }
        }
        System.out.println("motSecret : " + motSecret);
    }

    private void mettreAJourMotSecret(String mode, int taille) {
        motSecret = null;
        inputField.setText("");
        validerBtn.setEnabled(false);
        progressionLabel.setText("0/6-9"); // !!! Toujours afficher 0/6-9
        progressionLabel.setForeground(Color.RED);

        if ("🤖".equals(mode)) {
            OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
            dicoMots = db.getOnePhrase(taille);
        }
    }

    @SuppressWarnings("unused")
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

        JRadioButtonMenuItem reel = new JRadioButtonMenuItem("Réaliste");
        reel.addActionListener(e -> {
            currentBackgroundImage = "images/reel.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        JRadioButtonMenuItem reelHiver = new JRadioButtonMenuItem("Réaliste 2");
        reelHiver.addActionListener(e -> {
            currentBackgroundImage = "images/reelHiver.png";
            grillePanel.setBackgroundImage(currentBackgroundImage);
        });

        themeGroup.add(hiver); themeGroup.add(defaut);
        themeMenu.add(hiver); themeMenu.add(defaut);
        themeGroup.add(reel); themeGroup.add(reelHiver);
        themeMenu.add(reel); themeMenu.add(reelHiver);

        JCheckBoxMenuItem tutorielItem = new JCheckBoxMenuItem("Tutoriel");
        tutorielMenuItem = tutorielItem; // Stocker la référence

        // fond coché/décoché
        tutorielItem.addItemListener(e -> {
            if (tutorielItem.isSelected()) {
                tutorielItem.setBackground(new Color(0, 120, 255)); // Bleu
                tutorielItem.setForeground(Color.WHITE); // Texte blanc
            } else {
                tutorielItem.setBackground(null); // Fond défaut
                tutorielItem.setForeground(null); // Texte défaut
            }
        });

        tutorielItem.addActionListener(e -> {
            if (!tutorielActif) {
                // mode tutoriel
                grillePanel.setLignes(1);
                essaisMax = 1;
                grillePanel.setColonnes(8); // "TUTORIEL" = 8 lettres
                currentBackgroundImage = "images/tutoriel.png";
                tutorielActif = true;
                motSecret = "TUTORIEL";
                tutorielItem.setSelected(true);
                resetChamp();
                try {
                    new EcranRegle(this);
                } catch (FileNotFoundException e1) {}
            } else {
                grillePanel.setLignes(6);
                essaisMax = 6;
                grillePanel.setColonnes(6);
                currentBackgroundImage = "images/defaut.png";
                tutorielActif = false;
                motSecret = null;
                tutorielItem.setSelected(false);
                resetChamp();
            }
        });

        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.addActionListener(e -> System.exit(0));

        menu.add(essaisMenu);
        menu.add(themeMenu);
        menu.add(tutorielItem); // <-- coche automatique
        menu.add(quitter);
        return menu;
    }

    private void terminerJeu(boolean gagne, String message) {
        jeuTermine = true;
        inputField.setEnabled(false);
        validerBtn.setEnabled(false);

        if (gagne && message != null && !message.isEmpty()) {
            progressionLabel.setText(message);
            progressionLabel.setForeground(Color.GREEN);
        } else if (!gagne && message != null && !message.isEmpty()) {
            progressionLabel.setText(message);
            progressionLabel.setForeground(Color.RED);
        }
    }
}
