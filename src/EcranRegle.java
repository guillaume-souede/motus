import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class EcranRegle extends JFrame {
    private boolean jeuOuvert;

    public EcranRegle(boolean jeuOuvert) throws FileNotFoundException {
        super("Règles du Motus");
        this.jeuOuvert = jeuOuvert;

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
                if (jeuOuvert) {
                    dispose();
                } else {
                    new Main();
                    dispose();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(quiterButton, BorderLayout.LINE_END);

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

        // Ajouter un panneau pour la recherche de mots
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        // Créer un JComboBox avec les lettres de l'alphabet
        String[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        JComboBox<String> letterSelector = new JComboBox<>(alphabet);

        // Ajouter un ActionListener au JComboBox
        letterSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String letter = (String) letterSelector.getSelectedItem();

                try {
                    java.util.List<String> mots = new java.util.ArrayList<>();
                    java.nio.file.Files.lines(java.nio.file.Paths.get("data/motsMotus.txt"))
                        .filter(line -> line.toUpperCase().startsWith(letter))
                        .forEach(mots::add);

                    if (mots.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Aucun mot trouvé pour la lettre : " + letter);
                    } else {
                        // Convertir les mots en tableau pour JTable
                        String[][] data = new String[mots.size()][1];
                        for (int i = 0; i < mots.size(); i++) {
                            data[i][0] = mots.get(i);
                        }

                        // Colonnes du tableau
                        String[] columnNames = {"Mots"};

                        // Créer et afficher la JTable dans une nouvelle fenêtre
                        JTable table = new JTable(data, columnNames);
                        JScrollPane scrollPane = new JScrollPane(table);
                        table.setFillsViewportHeight(true);

                        JFrame tableFrame = new JFrame("Mots commençant par " + letter);
                        tableFrame.add(scrollPane);
                        tableFrame.setSize(400, 300);
                        tableFrame.setLocationRelativeTo(null);
                        tableFrame.setVisible(true);
                    }
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage());
                }
            }
        });

        // Ajouter les composants au panneau de recherche
        searchPanel.add(new JLabel("Dico' rapide :"));
        searchPanel.add(letterSelector);

        // Ajouter le panneau de recherche au haut de la fenêtre
        add(searchPanel, BorderLayout.NORTH);

        // Paramétrage fenêtre
        setSize(600, 600);
        setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException {
        new EcranRegle(false);
    }
}
