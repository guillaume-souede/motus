import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
		super("Menu principal Motus et bouche cousus");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(3,1,15,10));

		// création des éléments du menu
		JLabel titre = new JLabel("Bienvenue dans motus racine(4)");
		JButton bouttonJouer = new JButton("Jouer");
		JButton bouttonRegles = new JButton("Règles");
		JButton buttonFermer = new JButton("fermer");

		JPanel bouttonFrame = new JPanel(new FlowLayout(FlowLayout.CENTER,15,0));

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

		// positionnement des bouttons
		add(titre); 
		bouttonFrame.add(bouttonJouer);	bouttonFrame.add(bouttonRegles);
		add(bouttonFrame);
		add(buttonFermer);

		// paramétrage taille et position de la fenêtre
		setSize(200,250);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}