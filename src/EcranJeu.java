import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class EcranJeu extends JFrame {
    private final int essaisMax = 9;
    private final String motSecret;
    private final ArrayList<String> propositions = new ArrayList<>();
    private final GrilleMotusPanel grillePanel;
    private final JTextField inputField = new JTextField(10);
    private final JButton validerBtn = new JButton("Valider");

    public EcranJeu(String motSecret, String bgPath) {
        super("Motus - GUI");
        this.motSecret = motSecret.toUpperCase();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Taille fenêtre
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                setTitle("Motus - GUI (" + width + "x" + height + ")");
            }
        });

        // Grille et fond
        grillePanel = new GrilleMotusPanel(6, motSecret.length(), bgPath);
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
        JButton paramBtn = new JButton("⎈"); // paramètres
        JButton newGameBtn = new JButton("⟳"); // nouvelle partie
        leftPanel.add(paramBtn);
        leftPanel.add(newGameBtn);

        // BoutonsD
        JPanel rightPanel = new JPanel();
        JButton quitBtn = new JButton("⏻");
        quitBtn.addActionListener(e -> System.exit(0));
        rightPanel.add(quitBtn);

        // Footer
        // Resoudre polices d'écriture / taille !
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Proposition :"));
        centerPanel.add(inputField);
        centerPanel.add(validerBtn);

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
    }

    private void traiterProposition() {
        String prop = inputField.getText().trim().toUpperCase();
        if (prop.length() != motSecret.length()) {
            JOptionPane.showMessageDialog(this, "Le mot doit faire " + motSecret.length() + " lettres.");
            return;
        }
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
        }
        inputField.setText("");
    }

    public static void main(String[] args) {
        String bgPath = "images/apImage2.png";
        SwingUtilities.invokeLater(() -> new EcranJeu("MOTUS", bgPath));
    }
}

class GrilleMotusPanel extends JPanel {
    // Permet le "remplissage" avec propostions du joueur
    private final int lignes, colonnes;
    private final String bgPath;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Arrière-plan
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Mise à l'échelle
        double echelleX = getWidth() / 1536.0;
        double echelleY = getHeight() / 1024.0;

        caseLargeur = (int) (84 * echelleX);
        caseHauteur = (int) (90 * echelleY);
        int espacementX = (int) (3 * echelleX);
        int espacementY = (int) (3 * echelleY);
        grilleX = (int) (348 * echelleX);
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
                    // Cases vides bleu transparent
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