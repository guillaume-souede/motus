import java.awt.*;
import java.io.File;
import javax.swing.*;

public class EcranJeu extends JPanel {
    private final Image background;
    private final char[][] lettres;

    private int grilleX;
    private int grilleY;
    private int caseLargeur;
    private int caseHauteur;
    private final int spacing = 10;
    private final int rows = 3;
    private final int cols = 9;

    public EcranJeu(String bgPath, String[] mots) {
        File imageFile = new File(bgPath);
        if (!imageFile.exists()) {
            System.err.println("Image non trouvée : " + bgPath);
        }
        background = new ImageIcon(bgPath).getImage();
        
        lettres = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            char[] motChars = mots[i].toCharArray();
            System.arraycopy(motChars, 0, lettres[i], 0, Math.min(motChars.length, cols));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // Image de base : (1536x1024)
        double echelleX = getWidth() / 1536.0;
        double echelleY = getHeight() / 1024.0;

        // Cases
        caseLargeur = (int) (89 * echelleX);
        caseHauteur = (int) (96 * echelleY);

        // Espacement cases
        int espacementFinalX = (int) (10 * echelleX);
        int espacementFinalY = (int) (10 * echelleY);

        // Coin sup G gauche de la 1ière case
        grilleX = (int) (348 * echelleX);
        grilleY = (int) (507 * echelleY);

        int fontSize = caseHauteur * 2 / 3;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = grilleX + j * (caseLargeur + espacementFinalX);
                int y = grilleY + i * (caseHauteur + espacementFinalY);

                // BORDURES CASES
                // g.setColor(Color.BLACK);
                // g.drawRect(x, y, caseLargeur, caseHauteur);

                if (lettres[i][j] != '\0') {
                    g.setFont(new Font("Arial", Font.BOLD, fontSize));
                    g.drawString(String.valueOf(lettres[i][j]), x + caseLargeur / 4, y + caseHauteur * 2 / 3);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Path to the background image
        String bgPath = "images/apImage2.png";

        String[] mots = {"MOTUS", "AMOUROUX", "SOUEDE"};

        JFrame frame = new JFrame("Motus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        EcranJeu panel = new EcranJeu(bgPath, mots);
        frame.add(panel);

        frame.setVisible(true);
    }
}