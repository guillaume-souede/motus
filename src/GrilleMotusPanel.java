import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class GrilleMotusPanel extends JPanel {
    private int lignes;
    private int colonnes;
    private Image bgImage;
    private ArrayList<String> propositions = new ArrayList<>();
    private String motSecret = "";

    private final Font font = new Font("Arial", Font.BOLD, 32);
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

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        int largeurCase = getWidth() / colonnes;
        int hauteurCase = getHeight() / lignes;

        for (int i = 0; i < propositions.size(); i++) {
            String mot = propositions.get(i);
            for (int j = 0; j < mot.length(); j++) {
                char c = mot.charAt(j);
                int x = j * largeurCase;
                int y = i * hauteurCase;

                Color couleur = bleu;
                if (j < motSecret.length()) {
                    if (c == motSecret.charAt(j)) {
                        couleur = rouge;
                    } else if (motSecret.contains(String.valueOf(c))) {
                        couleur = jaune;
                    }
                }

                g.setColor(couleur);
                g.fillRoundRect(x + 5, y + 5, largeurCase - 10, hauteurCase - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.setFont(font);
                g.drawString(String.valueOf(c), x + largeurCase / 3, y + hauteurCase / 2 + 10);
            }
        }
    }
}
