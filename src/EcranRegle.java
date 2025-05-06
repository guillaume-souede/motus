import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollBar;

public class EcranRegle extends JFrame{

    BufferedReader fichierregle = new BufferedReader(new FileReader("data/rules.txt"));
    
    public EcranRegle() throws IOException{
        super("table des rêgles") ;

        // paramétrage de base du layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new FlowLayout(1));
        add(new JScrollBar());

		// création des éléments du menu
        JEditorPane paneRegles = new JEditorPane();
        paneRegles.setContentType("text/plain");
        paneRegles.read(fichierregle,null);
        paneRegles.isEditable();

        JButton quiterButton = new JButton("quitter");

        // paramétrage du bouton

		quiterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent clic) {
				new MainMenu();
				dispose();
            }
        });

        // ajout des boutons 
        add(paneRegles);
        add(quiterButton);


        pack();
        setVisible(true);

    }

    public static void main(String[] args) throws IOException {
        new EcranRegle();
    }
}