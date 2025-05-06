import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainMenu extends JFrame{

    /**
     * Menu principal qui est afficher au boot
     */
    
    public MainMenu(){
		// paramétrage de base du layout
		super("Menu principal Motus et bouche cousus");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(3,1,15,15));

		// création des éléments du menu
		JLabel titre = new JLabel("Bienvenue dans motus racine(4)");
		JButton bouttonJouer = new JButton("Jouer");
		JButton bouttonregle = new JButton("Règles");
		JButton buttonFermer = new JButton("fermer");

		// paramétrage des boutons

		bouttonJouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
				new EcranParametres();
				dispose();
            }
        });

		bouttonregle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
				try {
					new EcranRegle();
				} catch (IOException e) {
					dispose();
					e.printStackTrace();
				}
				dispose();
            }
        });

		// positionnement des bouttons

		add(titre); 
		add(bouttonJouer); add(bouttonregle);
		add(buttonFermer);

		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new MainMenu();
	}
}