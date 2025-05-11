import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.*;

public class EcranRegle extends JFrame {

    public EcranRegle() throws FileNotFoundException {
        super("Règles du Motus");

        // layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea paneRegles = new JTextArea();
        String stringRegle = OuvrirDB.lireRegle("data/rules.txt");
        paneRegles.setText(stringRegle);
        paneRegles.setEditable(false);
        paneRegles.setLineWrap(true); // retour à la ligne
        paneRegles.setWrapStyleWord(true); // couper les lignes
        paneRegles.setFont(new Font("Arial", Font.PLAIN, 14)); 
        paneRegles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // marges

        // JScrollPane pour le défilement
        JScrollPane scrollPane = new JScrollPane(paneRegles);

        // Bouton retour
        JButton quiterButton = new JButton("Retour");
        quiterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
                new Main();
                dispose();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(quiterButton, BorderLayout.PAGE_END);

        // ajout jaquettes jeux
        JPanel jaquettesPanel = new JPanel();
        jaquettesPanel.setLayout(new FlowLayout()); //FlowLayout pour alignement horiz

        String[] images = {"images/crevettes1.jpeg", "images/crevettes2.jpeg", "images/crevettes3.jpeg", "images/crevettes4.jpeg"};

        // Redimensionnement jaquettes jeux
        for (String imagePath : images) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance((int)(originalIcon.getIconWidth() * 0.2), 
                                                     (int)(originalIcon.getIconHeight() * 0.2), 
                                                     Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg); 
            JLabel imageLabel = new JLabel(scaledIcon);
            jaquettesPanel.add(imageLabel);
        }

        // Ajouter le titre et le panneau des jaquettes sous les règles
        add(jaquettesPanel, BorderLayout.PAGE_END);

        // Paramétrage fenêtre
        setSize(600, 600);
        setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException {
        new EcranRegle();
    }
}
