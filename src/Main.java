import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends JFrame{

    /**
     * Menu principal qui est afficher au boot
     */

    public Main(){
        // paramétrage de base du layout
        super("Motus et bouche cousue");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // chargement de l'image de fond
        ImageIcon icon = new ImageIcon("images/bienvenue.png");
        Image backgroundImage = icon.getImage();

        // panneau principal avec image de fond
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }; 
        backgroundPanel.setLayout(new GridLayout(3,1,15,10)); // même layout que l'ancien
        setContentPane(backgroundPanel);

        // création des éléments du menu
        JLabel titre = new JLabel("<html><center>Bienvenue dans Motus, une production √4 !</center></html>", JLabel.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 14));
        titre.setForeground(Color.WHITE); // texte blanc pour la lisibilité

        JButton bouttonJouer = new JButton("Jouer");
        JButton bouttonRegles = new JButton("Règles");
        JButton buttonFermer = new JButton("Fermer");

        // uniformisation des tailles de boutons
        Dimension buttonSize = new Dimension(100, 30);
        bouttonJouer.setPreferredSize(buttonSize);
        bouttonRegles.setPreferredSize(buttonSize);
        buttonFermer.setPreferredSize(buttonSize);

        JPanel bouttonFrame = new JPanel(new FlowLayout(FlowLayout.CENTER,15,0));
        bouttonFrame.setOpaque(false); // pour ne pas cacher l’image de fond

        // paramétrage des boutons
        bouttonJouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
                // ouvrir la fenêtre de paramètre de jeu
                new EcranParametres();
                dispose();
            }
        });

        bouttonRegles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
                try {
                    // ouvrir la fenêtre de règle
                    new EcranRegle();
                    dispose();
                } catch (IOException e) {
                    dispose();
                    e.printStackTrace();
                }
            }
        });

        buttonFermer.addActionListener(e -> dispose());

        // positionnement des bouttons
        bouttonFrame.add(bouttonJouer);
        bouttonFrame.add(bouttonRegles);
        bouttonFrame.add(buttonFermer);

        backgroundPanel.add(titre); 
        backgroundPanel.add(new JLabel()); // ligne vide pour espacement
        backgroundPanel.add(bouttonFrame);

        // paramétrage taille et position de la fenêtre
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
