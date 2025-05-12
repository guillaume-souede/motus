import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class JoueurProposeMot extends JFrame{

    String mot;
    JTextField champ;
    JButton confirmButton;
    String prop;
    
    public String getMot() {
        return mot;
    }

    public String getProp() {
        return prop;
    }

    JoueurProposeMot(){
    super("entrez le mot secret :");

    // création des éléments
    confirmButton = new JButton("confirmer");
    champ = new JTextField(12);

    // layout
    setLayout(new BorderLayout());

    // ajout sur le layout
    add(confirmButton, BorderLayout.PAGE_END);
    add(champ, BorderLayout.CENTER);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(275, 85);
    setLocationRelativeTo(null);
    setVisible(true);

    confirmButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent clic) {
            mot = champ.getText().toUpperCase();
            dispose();
            }
        });
    mot = champ.getText().trim().toUpperCase();
    }

    public static void main(String[] args) {
        new JoueurProposeMot();
    }
}



