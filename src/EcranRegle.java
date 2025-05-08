import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class EcranRegle extends JFrame{

    
    public EcranRegle() throws FileNotFoundException{
        super("table des rêgles") ;

        // paramétrage de base du layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		// création des éléments du menu
        JTextArea paneRegles = new JTextArea();
        String stringRegle = OuvrirDB.lireRegle("data/rules.txt");
        paneRegles.setText(stringRegle);
        paneRegles.setEditable(false);

        JButton quiterButton = new JButton("retour");

        // paramétrage du bouton
		quiterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
				new Main();
				dispose();
            }
        });

        // ajout des boutons 
        add(paneRegles,BorderLayout.CENTER);
        add(quiterButton,BorderLayout.PAGE_END);

        // paramétrage fenêtre
        setSize(700,250);
        setLocationRelativeTo(null);
        setVisible(true);
    
    }

    public static void main(String[] args) throws IOException {
        new EcranRegle();
    }
}