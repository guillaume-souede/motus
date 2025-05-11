import java.awt.*;
import javax.swing.*;

public class NosAutresJeux extends JFrame {

    public NosAutresJeux() {
        super("Nos autres jeux");

        // Paramétrage de base du layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout()); // Utilisation de FlowLayout pour un alignement horizontal

        // Tableau de noms d'images
        String[] images = {"images/crevettes1.jpeg", "images/crevettes2.jpeg", "images/crevettes3.jpeg", "images/crevettes4.jpeg"};

        // Chargement et redimensionnement des images
        for (String imagePath : images) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image img = originalIcon.getImage(); // Obtenir l'objet Image
            Image scaledImg = img.getScaledInstance((int)(originalIcon.getIconWidth() * 0.2), 
                                                     (int)(originalIcon.getIconHeight() * 0.2), 
                                                     Image.SCALE_SMOOTH); // Redimensionner à 20% de la taille originale
            ImageIcon scaledIcon = new ImageIcon(scaledImg); // Créer un nouveau ImageIcon à partir de l'image redimensionnée
            JLabel imageLabel = new JLabel(scaledIcon);
            add(imageLabel); // Ajouter l'image au JFrame
        }

        // Paramétrage de la fenêtre
        setSize(600, 260); // Taille de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre
        setVisible(true); // Rendre la fenêtre visible
    }

    public static void main(String[] args) {
        new NosAutresJeux(); // Créer et afficher la fenêtre
    }
}
