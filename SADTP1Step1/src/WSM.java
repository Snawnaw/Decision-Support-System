import javax.swing.*;
import java.awt.*;

//Etap 1
public class WSM {

    public WSM() {
        JFrame frame = new JFrame("Évaluation WSM - Fournisseur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLayout(new BorderLayout(10, 10));

        String[] criteres = {"Coût", "Qualité", "Délai"};
        int nbCrit = criteres.length;

        JPanel inputPanel = new JPanel(new GridLayout(nbCrit, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField[] poidsFields = new JTextField[nbCrit];
        JTextField[] scoresFields = new JTextField[nbCrit];

        for (int i = 0; i < nbCrit; i++) {
            JPanel criterePanel = new JPanel(new GridLayout(2, 2, 5, 5)); 
            criterePanel.setBorder(BorderFactory.createTitledBorder(criteres[i])); 

            criterePanel.add(new JLabel("Poids (0.0 - 1.0):"));
            poidsFields[i] = new JTextField();
            criterePanel.add(poidsFields[i]);

            criterePanel.add(new JLabel("Score (1-10):"));
            scoresFields[i] = new JTextField();
            criterePanel.add(scoresFields[i]);

            inputPanel.add(criterePanel);
        }

        JButton btnCalculate = new JButton("Calculer");

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnCalculate, BorderLayout.SOUTH);

        btnCalculate.addActionListener(e -> {
            try {
                double[] poids = new double[nbCrit];
                int[] scores = new int[nbCrit];
                double sommePoids = 0;

                for (int i = 0; i < nbCrit; i++) {
                    poids[i] = Double.parseDouble(poidsFields[i].getText());
                    if (poids[i] <= 0 || poids[i] >= 1) {
                        JOptionPane.showMessageDialog(frame, "Les poids doivent être des réels dans ]0,1[.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    sommePoids += poids[i];
                }

                if (Math.abs(sommePoids - 1.0) > 0.001) {
                    JOptionPane.showMessageDialog(frame, "La somme des poids doit être égale à 1.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (int i = 0; i < nbCrit; i++) {
                    scores[i] = Integer.parseInt(scoresFields[i].getText());
                    if (scores[i] < 1 || scores[i] > 10) {
                        JOptionPane.showMessageDialog(frame, "Les scores doivent être des entiers entre 1 et 10.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                double scorePondereTotal = 0;
                for (int i = 0; i < nbCrit; i++) {
                    scorePondereTotal += scores[i] * poids[i];
                }

                textArea.setText("Résultats de l'évaluation WSM:\n\n");
                for (int i = 0; i < nbCrit; i++) {
                    textArea.append(criteres[i] + ":\n");
                    textArea.append("Poids: " + poids[i] + "\n");
                    textArea.append("Score: " + scores[i] + "\n\n");
                }
                textArea.append("Score pondéré total: " + String.format("%.2f", scorePondereTotal));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Veuillez entrer des valeurs valides pour les poids et les scores.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WSM::new);
    }
}
