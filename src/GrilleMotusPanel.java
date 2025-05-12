import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class GrilleMotusPanel extends JPanel {
    private int lignes;
    private int colonnes;
    private Image bgImage;
    private ArrayList<String> propositions = new ArrayList<>();
    private String motSecret = "";

    private final Color rouge = Color.decode("#de4649");
    private final Color jaune = Color.decode("#deb138");
    private final Color bleu = Color.decode("#329ddc");

    public GrilleMotusPanel(int lignes, int colonnes, String bgPath) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        setBackgroundImage(bgPath);
        setOpaque(false);
        setPreferredSize(new Dimension(600, 600));
    }

    public void setLignes(int lignes) {
        this.lignes = lignes;
        repaint();
    }

    public void setColonnes(int colonnes) {
        this.colonnes = colonnes;
        repaint();
    }

    public int getColonnes() {
        return colonnes;
    }

    public void setBackgroundImage(String imagePath) {
        try {
            this.bgImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            this.bgImage = null;
        }
        repaint();
    }

    public void majGrille(ArrayList<String> propositions, String motSecret) {
        this.propositions = new ArrayList<>(propositions);
        this.motSecret = motSecret;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessiner l'image de fond
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Facteurs de mise à l'échelle
        double echelleX = getWidth() / 1536.0;
        double echelleY = getHeight() / 1024.0;

        int caseLargeur = (int) (84 * echelleX);
        int caseHauteur = (int) (90 * echelleY);
        int espacementX = (int) (3 * echelleX);
        int espacementY = (int) (3 * echelleY);

        // Centrage de la grille sur l'arrière-plan (AP)
        int largeurGrille = colonnes * caseLargeur + (colonnes - 1) * espacementX;
        int intervalleMin = (int) (341 * echelleX);
        int intervalleMax = (int) (1137 * echelleX);
        int grilleX = intervalleMin + (intervalleMax - intervalleMin - largeurGrille) / 2;
        int grilleY = (int) (450 * echelleY); // Position verticale ajustée

        // Définir la taille de la police
        int fontSize = caseHauteur * 2 / 3;
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(Color.WHITE);

        // Dessiner les cases de la grille
        for (int i = 0; i < lignes; i++) {
            String prop = (i < propositions.size()) ? propositions.get(i) : "";
            for (int j = 0; j < colonnes; j++) {
                int x = grilleX + j * (caseLargeur + espacementX);
                int y = grilleY + i * (caseHauteur + espacementY);

                // Déterminer la couleur de la case
                Color couleur = bleu;
                if (i < propositions.size() && j < prop.length()) {
                    char c = prop.charAt(j);
                    if (j < motSecret.length() && c == motSecret.charAt(j)) {
                        couleur = rouge;
                    } else if (motSecret.contains(String.valueOf(c))) {
                        couleur = jaune;
                    }
                }

                // Dessiner la case (sans bords arrondis)
                g.setColor(couleur);
                g.fillRect(x, y, caseLargeur, caseHauteur);

                // Dessiner le texte dans la case
                if (i < propositions.size() && j < prop.length()) {
                    char c = prop.charAt(j);
                    g.setColor(Color.WHITE);
                    drawCenteredString(g, String.valueOf(c), x, y, caseLargeur, caseHauteur);
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
