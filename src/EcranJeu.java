import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;

public class EcranJeu extends JFrame {
    private int essaisMax;
    private String motSecret;
    private final ArrayList<String> propositions = new ArrayList<>();
    private final GrilleMotusPanel grillePanel;
    private final JTextField inputField = new JTextField(10);
    private final JButton validerBtn = new JButton("Valider");
    private final JLabel progressionLabel = new JLabel("/"); // Par défaut, 0/6

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
        int lignes = 6; // Par défaut
        int colonnes = 6; // Par défaut
        grillePanel = new GrilleMotusPanel(lignes, colonnes, bgPath);
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

        // reset
        JButton resetBtn = new JButton("⟳");
        resetBtn.addActionListener(e -> {
            // du jeu
            String mode = (String) modeComboBox.getSelectedItem();
            int taille = (int) tailleComboBox.getSelectedItem();
            mettreAJourMotSecret(mode, taille);

            // des propositions et de grille
            propositions.clear();
            grillePanel.majGrille(propositions, motSecret);

            // des champs
            inputField.setEnabled(true);
            validerBtn.setEnabled(true);
            inputField.setText("");
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
            mettreAJourMotSecret(mode, taille);

            // MàJ grille
            grillePanel.setColonnes(taille);
            grillePanel.repaint();
        });

        // ComposantsG
        leftPanel.add(new JLabel("Taille:"));
        leftPanel.add(tailleComboBox);
        leftPanel.add(new JLabel("Mode:"));
        leftPanel.add(modeComboBox);
        leftPanel.add(resetBtn); // Ajouter le bouton reset

        // BoutonsD
        JPanel rightPanel = new JPanel();
        JButton quitBtn = new JButton("⏻");
        quitBtn.addActionListener(e -> System.exit(0));
        rightPanel.add(quitBtn);

        // Footer
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Proposition :"));
        centerPanel.add(inputField);
        centerPanel.add(progressionLabel); // voir progrès mot
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
        modeComboBox.setSelectedIndex(0); // Déclencher l'initialisation
        validerBtn.setEnabled(false); // Désactiver le bouton "Valider" au démarrage
    }

    private void traiterProposition() {
        String prop = inputField.getText().trim().toUpperCase();

        if (propositions.size() >= essaisMax) return;

        propositions.add(prop);
        grillePanel.majGrille(propositions, motSecret);

        if (prop.equals(motSecret)) {
            JOptionPane.showMessageDialog(this, "Bravo ! Motus trouvé en " + propositions.size() + " essais.");
            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
        } else if (propositions.size() == essaisMax) {
            JOptionPane.showMessageDialog(this, "Perdu ! Le mot était : " + motSecret);
            inputField.setEnabled(false);
            validerBtn.setEnabled(false);
        } else {
            validerBtn.setEnabled(false); // désactive le bouton "Valider" après 1 tentative (vu que tentative = mot forcément ok)
        }
        inputField.setText("");
    }

    private void mettreAJourMotSecret(String mode, int taille) {
        if ("👨".equals(mode)) {
            // Mode 👨, mots tests
            switch (taille) {
                case 6 -> motSecret = "AVIONS";
                case 7 -> motSecret = "BONJOUR";
                case 8 -> motSecret = "CHOCOLAT";
                case 9 -> motSecret = "FAMILIALE";
            }
        } else if ("🤖".equals(mode)) {
            // Mode 🤖, mots tests
            motSecret = "CHIENNE"; // ICI IMPLEMENTER CODE !
        }
    }

    public static void main(String[] args) {
        String bgPath = "images/apImage2.png";
        SwingUtilities.invokeLater(() -> new EcranJeu(bgPath));
    }
}

class GrilleMotusPanel extends JPanel {
    // REMPLIR PROPOSITIONS
    private int lignes;
    private int colonnes;
    private String bgPath;
    private Image bgImage;
    private final Font font = new Font("Arial", Font.BOLD, 32);

    private ArrayList<String> propositions = new ArrayList<>();
    private String motSecret = "";

    private final Color rouge = Color.decode("#de4649");
    private final Color jaune = Color.decode("#deb138");
    private final Color bleu = Color.decode("#329ddc");

    private int caseLargeur, caseHauteur, grilleX, grilleY;

    public GrilleMotusPanel(int lignes, int colonnes, String bgPath) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.bgPath = bgPath;
        try {
            bgImage = new ImageIcon(bgPath).getImage();
        } catch (Exception e) {
            bgImage = null;
        }
        setPreferredSize(new Dimension(600, 600));
        setOpaque(false);
    }

    public void majGrille(ArrayList<String> props, String motSecret) {
        this.propositions = new ArrayList<>(props);
        this.motSecret = motSecret;
        repaint();
    }

    public void setColonnes(int colonnes) {
        this.colonnes = colonnes; // Met à jour le nombre de colonnes
        repaint(); // Redessine la grille
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Arrière-plan
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Scaling de l'AP
        double echelleX = getWidth() / 1536.0;
        double echelleY = getHeight() / 1024.0;

        caseLargeur = (int) (84 * echelleX);
        caseHauteur = (int) (90 * echelleY);
        int espacementX = (int) (3 * echelleX);
        int espacementY = (int) (3 * echelleY);

        // Centrage Grille sur l'AP
        int largeurGrille = colonnes * caseLargeur + (colonnes - 1) * espacementX;
        int intervalleMin = (int) (341 * echelleX);
        int intervalleMax = (int) (1137 * echelleX);
        grilleX = intervalleMin + (intervalleMax - intervalleMin - largeurGrille) / 2;

        grilleY = (int) (450 * echelleY); // ok

        int fontSize = caseHauteur * 2 / 3;
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(Color.WHITE);

        for (int i = 0; i < lignes; i++) {
            String prop = (i < propositions.size()) ? propositions.get(i) : "";
            String etat = (i < propositions.size()) ? EtatMot.checkEtatMot(prop, motSecret) : "";
            String malPlaces = (i < propositions.size()) ? EtatMot.checkWrongPlacement(prop, motSecret) : "";

            for (int j = 0; j < colonnes; j++) {
                int x = grilleX + j * (caseLargeur + espacementX);
                int y = grilleY + i * (caseHauteur + espacementY);

                if (i < propositions.size() && !prop.isEmpty()) {
                    char c = prop.charAt(j);
                    if (etat.charAt(j) == c) {
                        g.setColor(rouge);
                        g.fillRect(x, y, caseLargeur, caseHauteur);
                    } else if (malPlaces.indexOf(c) != -1) {
                        g.setColor(jaune);
                        g.fillOval(x, y, caseLargeur, caseHauteur);
                    } else {
                        g.setColor(bleu);
                        g.fillRect(x, y, caseLargeur, caseHauteur);
                    }

                    g.setColor(Color.WHITE);
                    drawCenteredString(g, "" + c, x, y, caseLargeur, caseHauteur);
                } else {
                    // Cases vides == bleu transparent
                    g.setColor(bleu);
                    g.fillRect(x, y, caseLargeur, caseHauteur);
                }
            }
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, int w, int h) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int tx = x + (w - metrics.stringWidth(text)) / 2;
        int ty = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, tx, ty);
    }
}