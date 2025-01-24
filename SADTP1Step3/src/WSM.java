import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

//Etape 2/3
public class WSM {

    public WSM() {
        JFrame frame = new JFrame("WSM Evaluation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 2));
        JLabel labelNbCrit = new JLabel("Nombre de critères:");
        JTextField textNbCrit = new JTextField();
        JLabel labelNbAlt = new JLabel("Nombre d'alternatives:");
        JTextField textNbAlt = new JTextField();
        panel.add(labelNbCrit);
        panel.add(textNbCrit);
        panel.add(labelNbAlt);
        panel.add(textNbAlt);

        JButton btnSubmit = new JButton("Submit");
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnSubmit, BorderLayout.SOUTH);

        btnSubmit.addActionListener(e -> {
            try {
                int nbCrit = Integer.parseInt(textNbCrit.getText());
                int nbAlt = Integer.parseInt(textNbAlt.getText());

                if (nbCrit <= 1) {
                    JOptionPane.showMessageDialog(frame, "Veuillez entrer au monis deux critères", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (nbAlt <= 1) {
                    JOptionPane.showMessageDialog(frame, "Veuillez entrer au moins deux alternatives pour pouvoir les comparer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] criteres = getInputStrings("Veuillez introduire le nom du critère", nbCrit, frame);

                for (int i = 0; i < nbCrit; i++) {
                    for (int j = i + 1; j < nbCrit; j++) {
                        if (criteres[i].equals(criteres[j])) {
                            JOptionPane.showMessageDialog(frame, "Les noms des critères doivent être uniques.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                String[] alternatives = getInputStrings("Veuillez introduire le nom de l'alternative", nbAlt, frame);

                for (int i = 0; i < nbAlt; i++) {
                    for (int j = i + 1; j < nbAlt; j++) {
                        if (alternatives[i].equals(alternatives[j])) {
                            JOptionPane.showMessageDialog(frame, "Les noms des alternatives doivent être uniques.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                int[][] evaluations = new int[nbAlt][nbCrit];
                for (int i = 0; i < nbAlt; i++) {
                    for (int j = 0; j < nbCrit; j++) {
                        evaluations[i][j] = getInputInt("Score de " + alternatives[i] + " pour " + criteres[j] + " (1-10):", 1, 10, frame);
                    }
                }

                int[] poids = new int[nbCrit];
                for (int i = 0; i < nbCrit; i++) {
                    poids[i] = getInputInt("Poids pour " + criteres[i] + " (1-100):", 1, 100, frame);
                }

                int sommePoids = 0;
                for (int i = 0; i < nbCrit; i++) {
                    sommePoids += poids[i];
                }
                if (sommePoids != 100) {
                    JOptionPane.showMessageDialog(frame, "La somme des poids doit être égale à 100.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double[] scoresPonderes = new double[nbAlt];
                for (int i = 0; i < nbAlt; i++) {
                    for (int j = 0; j < nbCrit; j++) {
                        scoresPonderes[i] += (evaluations[i][j] * poids[j])/100;
                    }
                }

                Integer[] indices = new Integer[nbAlt];
                for (int i = 0; i < nbAlt; i++) indices[i] = i;
                Arrays.sort(indices, Comparator.comparingDouble(i -> -scoresPonderes[i]));

                StringBuilder result = new StringBuilder("Classement des alternatives:\n");
                for (int i = 0; i < nbAlt; i++) {
                    result.append((i + 1) + ". " + alternatives[indices[i]] + " avec un score pondéré de " + scoresPonderes[indices[i]] + "\n");
                }
                result.append("La meilleure alternative est: " + alternatives[indices[0]]);
                textArea.setText(result.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Veuillez entrer des nombres valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private String[] getInputStrings(String prompt, int count, JFrame frame) {
        String[] inputs = new String[count];
        for (int i = 0; i < count; i++) {
            String input;
            do {
                input = JOptionPane.showInputDialog(prompt + " " + (i + 1) + ":");
                if (input == null || input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "La saisie ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } while (input == null || input.trim().isEmpty());
            inputs[i] = input;
        }
        return inputs;
    }

    private int getInputInt(String prompt, int min, int max, JFrame frame) {
        int value;
        do {
            try {
                value = Integer.parseInt(JOptionPane.showInputDialog(prompt));
                if (value < min || value > max) {
                    JOptionPane.showMessageDialog(frame, "La valeur doit être comprise entre " + min + " et " + max + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                value = -1;
                JOptionPane.showMessageDialog(frame, "Veuillez entrer un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } while (value < min || value > max);
        return value;
    }

    public static void main(String[] args) {
        new WSM();
    }
}






