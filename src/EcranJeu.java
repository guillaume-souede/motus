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
            mettreAJourMotSecret(selectedMode, taille);

            if ("🤖".equals(selectedMode)) {
                inputField.setEnabled(true); // Enable input field for AI mode
                validerBtn.setEnabled(true); // Disable validation button until input is provided
            } else {
                inputField.setEnabled(true); // Disable input field for player mode
                validerBtn.setEnabled(true);
            }
        });

        resetBtn.addActionListener(e -> {
            int taille = grillePanel.getColonnes();
            mettreAJourMotSecret((String) modeComboBox.getSelectedItem(), taille);
            resetChamp(taille);
        });

        inputField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int currentLength = inputField.getText().length();
                int targetLength = motSecret.length();
                progressionLabel.setText(currentLength + "/" + targetLength);
                progressionLabel.setForeground(currentLength == targetLength ? Color.GREEN : Color.RED);
                validerBtn.setEnabled(currentLength == targetLength);
            }
        });

        validerBtn.addActionListener(e -> traiterProposition(mode));
        inputField.addActionListener(e -> traiterProposition(mode));

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        modeComboBox.setSelectedIndex(0);
        validerBtn.setEnabled(false);
    }

    private void resetChamp(int taille) {
        propositions.clear();
        grillePanel.majGrille(propositions, motSecret);
        grillePanel.setBackgroundImage(currentBackgroundImage);
        inputField.setEnabled(true);
        validerBtn.setEnabled(false);
        inputField.setText("");
        progressionLabel.setText("0/" + taille);
    }

    private void traiterProposition(String mode) {
        if ("👨".equals(mode)) {
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
        else {
            propositions.add("null");
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
        } 
        else {// à remplacer par une IA plus tard
            motSecret = inputField.getText().trim().toUpperCase();

            // initialisation des variables pour le mode IA
            progVraie = motSecret.trim(); // sde la doc : permet de faire du slicing et extraire la première lettre du mot
            charsMalPlace = new HashMap<>();
            charImpossible = "";
            OuvrirDB db = new OuvrirDB("data/motsMotus.txt");
            dicoMots = new ArrayList<>(db.getOnePhrase(taille));
            int essais = 0;

            do {
                dicoMots = LogiqueBot.choix(mode, charsMalPlace, mode, dicoMots);
                String proposition = LogiqueBot.randomWord(dicoMots);
                propositions.add(proposition);

                if (proposition.equals(motSecret)) {
                    
                    jeuTermine = true;
                    break;
                }
                essais++;
            } while (!progVraie.equals(motSecret) && essais < essaisMax || jeuTermine == false);
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
