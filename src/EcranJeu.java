import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class EcranJeu extends JFrame {
    private final int essaisMax = 9;
    private final String motSecret;
    private final ArrayList<String> propositions = new ArrayList<>();
    private final GrilleMotusPanel grillePanel;
    private final JTextField champSaisie = new JTextField(10);
    private final JButton boutonValider = new JButton("Valider");

    public EcranJeu(String motSecret, String cheminImageFond) {
        super("Motus - Interface Graphique");
        this.motSecret = motSecret.toUpperCase();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Titre dynamique (Taille fenêtre)
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int largeur = getWidth();
                int hauteur = getHeight();
                setTitle("Motus - Interface Graphique (" + largeur + "x" + hauteur + ")");
            }
        });

        // Grille et fond
        grillePanel = new GrilleMotusPanel(6, motSecret.length(), cheminImageFond);
        JScrollPane panneauDefilement = new JScrollPane(grillePanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panneauDefilement.getViewport().setOpaque(false);
        panneauDefilement.setOpaque(false);
        panneauDefilement.getVerticalScrollBar().setUnitIncrement(16);
        panneauDefilement.setBorder(null);
        add(panneauDefilement, BorderLayout.CENTER);

        // Panneau pour les boutons et la saisie
        JPanel panneauSaisie = new JPanel(new BorderLayout());

        // BoutonsG
        JPanel panneauGauche = new JPanel();
        JButton boutonParametres = new JButton("⎈"); // paramètres
        JButton boutonNouvellePartie = new JButton("⟳"); // nouvelle partie
        panneauGauche.add(boutonParametres);
        panneauGauche.add(boutonNouvellePartie);

        // BoutonsD
        JPanel panneauDroit = new JPanel();
        JButton boutonQuitter = new JButton("⏻");
        boutonQuitter.addActionListener(e -> System.exit(0));
        panneauDroit.add(boutonQuitter);

        // Footer
        JPanel panneauCentre = new JPanel();
        panneauCentre.add(new JLabel("Proposition :"));
        panneauCentre.add(champSaisie);
        panneauCentre.add(boutonValider);

        panneauSaisie.add(panneauGauche, BorderLayout.WEST);
        panneauSaisie.add(panneauCentre, BorderLayout.CENTER);
        panneauSaisie.add(panneauDroit, BorderLayout.EAST);

        add(panneauSaisie, BorderLayout.SOUTH);

        boutonValider.addActionListener(e -> traiterProposition());
        champSaisie.addActionListener(e -> traiterProposition());

        // Taille fenêtre 900x700
        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void traiterProposition() {
        String proposition = champSaisie.getText().trim().toUpperCase();
        if (proposition.length() != motSecret.length()) {
            JOptionPane.showMessageDialog(this, "Le mot doit faire " + motSecret.length() + " lettres.");
            return;
        }
        if (propositions.size() >= essaisMax) return;

        propositions.add(proposition);
        grillePanel.mettreAJourGrille(propositions, motSecret);

        if (proposition.equals(motSecret)) {
            JOptionPane.showMessageDialog(this, "Bravo ! Motus trouvé en " + propositions.size() + " essais.");
            champSaisie.setEnabled(false);
            boutonValider.setEnabled(false);
        } else if (propositions.size() == essaisMax) {
            JOptionPane.showMessageDialog(this, "Perdu ! Le mot était : " + motSecret);
            champSaisie.setEnabled(false);
            boutonValider.setEnabled(false);
        }
        champSaisie.setText("");
    }

    public static void main(String[] args) {
        String cheminImageFond = "images/apImage2.png";
        SwingUtilities.invokeLater(() -> new EcranJeu("MOTUS", cheminImageFond));
    }
}

class GrilleMotusPanel extends JPanel {
    private final int lignes, colonnes;
    private final String cheminImageFond;
    private Image imageFond;
    private final Font police = new Font("Arial", Font.BOLD, 32);

    private ArrayList<String> propositions = new ArrayList<>();
    private String motSecret = "";

    private final Color rouge = Color.decode("#de4649");
    private final Color jaune = Color.decode("#deb138");
    private final Color bleu = Color.decode("#329ddc");

    private int largeurCase, hauteurCase, xGrille, yGrille;

    public GrilleMotusPanel(int lignes, int colonnes, String cheminImageFond) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.cheminImageFond = cheminImageFond;
        try {
            imageFond = new ImageIcon(cheminImageFond).getImage();
        } catch (Exception e) {
            imageFond = null;
        }
        setPreferredSize(new Dimension(600, 600));
        setOpaque(false);
    }

    public void mettreAJourGrille(ArrayList<String> propositions, String motSecret) {
        this.propositions = new ArrayList<>(propositions);
        this.motSecret = motSecret;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Arrière-plan
        if (imageFond != null) {
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), this);
        }

        // Échelle
        double echelleX = getWidth() / 1536.0;
        double echelleY = getHeight() / 1024.0;

        largeurCase = (int) (84 * echelleX);
        hauteurCase = (int) (90 * echelleY);
        int espacementX = (int) (3 * echelleX);
        int espacementY = (int) (3 * echelleY);
        xGrille = (int) (348 * echelleX);
        yGrille = (int) (450 * echelleY); // remontée un peu (noms joueurs enlevés)

        int taillePolice = hauteurCase * 2 / 3;
        g.setFont(new Font("Arial", Font.BOLD, taillePolice));
        g.setColor(Color.WHITE);

        for (int i = 0; i < lignes; i++) {
            String prop = (i < propositions.size()) ? propositions.get(i) : "";
            String etat = (i < propositions.size()) ? EtatMot.verifierEtatMot(prop, motSecret) : "";
            String malPlaces = (i < propositions.size()) ? EtatMot.verifierMauvaisPlacement(prop, motSecret) : "";

            for (int j = 0; j < colonnes; j++) {
                int x = xGrille + j * (largeurCase + espacementX);
                int y = yGrille + i * (hauteurCase + espacementY);

                if (i < propositions.size() && !prop.isEmpty()) {
                    char c = prop.charAt(j);
                    if (etat.charAt(j) == c) {
                        g.setColor(rouge);
                        g.fillRect(x, y, largeurCase, hauteurCase);
                    } else if (malPlaces.indexOf(c) != -1) {
                        g.setColor(jaune);
                        g.fillOval(x, y, largeurCase, hauteurCase);
                    } else {
                        g.setColor(bleu);
                        g.fillRect(x, y, largeurCase, hauteurCase);
                    }

                    g.setColor(Color.WHITE);
                    dessinerTexteCentre(g, "" + c, x, y, largeurCase, hauteurCase);
                } else {
                    // Cases vides == bleu transparent
                    g.setColor(bleu);
                    g.fillRect(x, y, largeurCase, hauteurCase);
                }
            }
        }
    }

    private void dessinerTexteCentre(Graphics g, String texte, int x, int y, int w, int h) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int tx = x + (w - metrics.stringWidth(texte)) / 2;
        int ty = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(texte, tx, ty);
    }
}

// EtatMot simplifié
class EtatMot {
    public static String verifierEtatMot(String proposition, String secret) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < proposition.length(); i++) {
            res.append(proposition.charAt(i) == secret.charAt(i) ? proposition.charAt(i) : '-');
        }
        return res.toString();
    }

    public static String verifierMauvaisPlacement(String proposition, String secret) {
        StringBuilder malPlaces = new StringBuilder();
        for (int i = 0; i < proposition.length(); i++) {
            char c = proposition.charAt(i);
            if (secret.contains("" + c) && secret.charAt(i) != c) {
                malPlaces.append(c);
            }
        }
        return malPlaces.toString();
    }
}
