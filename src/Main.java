import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends JFrame{

    // MENU PRINCIPAL AFFICHé AU BOOT.

    public Main(){
        // paramétrage de base du layout
        super("Motus et bouche cousue");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Image de fond
        ImageIcon icon = new ImageIcon("images/bienvenue.png");
        Image arrPlan = icon.getImage();

        // Panneau principal
        JPanel arrPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(arrPlan, 0, 0, getWidth(), getHeight(), this);
            }
        };
        arrPanel.setLayout(new GridLayout(3,1,15,10)); // même layout que l'ancien
        setContentPane(arrPanel);

        // Menu : création
        JLabel titre = new JLabel("<html><center>Bienvenue dans Motus, une production √4 !</center></html>", JLabel.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 14));
        titre.setForeground(Color.WHITE); // texte blanc pour la lisibilité

        JButton bouttonJouer = new JButton("Jouer");
        JButton bouttonRegles = new JButton("Règles");
        JButton buttonFermer = new JButton("Fermer");

        // Boutons : taille
        Dimension buttonSize = new Dimension(100, 30);
        bouttonJouer.setPreferredSize(buttonSize);
        bouttonRegles.setPreferredSize(buttonSize);
        buttonFermer.setPreferredSize(buttonSize);

        JPanel bouttonFrame = new JPanel(new FlowLayout(FlowLayout.CENTER,15,0));
        bouttonFrame.setOpaque(false); // pour ne pas cacher l’image de fond

        // Boutons : paramétrage
        bouttonJouer.addActionListener(clic -> {
            new EcranChargement();
            dispose();
        });

        bouttonRegles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
                try {
                    // passer la référence à la fenêtre Main
                    new EcranRegle(Main.this);
                } catch (Exception e) {
                    dispose();
                    e.printStackTrace();
                }
            }
        });

        buttonFermer.addActionListener(e -> dispose());

        // Bouttons : position
        bouttonFrame.add(bouttonJouer);
        bouttonFrame.add(bouttonRegles);
        bouttonFrame.add(buttonFermer);

        arrPanel.add(titre); 
        arrPanel.add(new JLabel()); // permet espacement Titre / Boutons
        arrPanel.add(bouttonFrame);

        // Fenêtre : position et paramétrage
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
