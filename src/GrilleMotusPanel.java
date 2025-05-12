import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

class GrilleMotusPanel extends JPanel {
    // REMPLIR PROPOSITIONS
    private int lignes;
    private int colonnes;
    private String bgPath;
    private Image bgImage;

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
        revalidate();
        repaint(); // Redessine la grille
        
    }

    public void setColonnes(int colonnes) {
        this.colonnes = colonnes;
        revalidate();
        repaint(); // Redessine la grille
        // update the Graphical interface
    }

    public void setLignes(int lignes) {
        this.lignes = lignes;
        revalidate();
        repaint(); // Redessine la grille
    }

    public int getColonnes() {
        return colonnes;
    }

    public void setBackgroundImage(String imagePath) {
        try {
            bgImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            bgImage = null;
        }
        revalidate();
        repaint(); // Redessiner la grille
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Arrière-plan (AP)
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
        setVisible(true);
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, int w, int h) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int tx = x + (w - metrics.stringWidth(text)) / 2;
        int ty = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, tx, ty);
    }
}