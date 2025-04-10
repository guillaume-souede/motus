import javax.swing.*;
import java.awt.*;
import java.io.File;

public class EcranJeu extends JPanel {
    private Image background;
    private char[][] lettres;

    private int gridX;
    private int gridY;
    private int caseSize;
    private int spacing = 10;
    private int rows = 3;
    private int cols = 9;

    public ImageOverlayPanel(String bgPath, String[] mots) {
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
        
        caseSize = Math.min(getWidth(), getHeight()) / 12;
        int fontSize = caseSize * 2 / 3;

        // Ajuster la position de la grille pour qu'elle commence à (300px, 255px)
        gridX = 255;
        gridY = 300;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = gridX + j * (caseSize + spacing);
                int y = gridY + i * (caseSize + spacing);
                
                g.setColor(Color.BLACK);
                g.drawRect(x, y, caseSize, caseSize);
                
                if (lettres[i][j] != '\0') {
                    g.setFont(new Font("Arial", Font.BOLD, fontSize));
                    g.drawString(String.valueOf(lettres[i][j]), x + caseSize / 4, y + caseSize * 2 / 3);
                }
            }
        }
    }
}
