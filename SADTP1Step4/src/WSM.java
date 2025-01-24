import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.*;

//Etape 4
public class WSM {

    public WSM() {
        JFrame frame = new JFrame("WSM Evaluation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        JLabel labelNbCrit = new JLabel("Nombre de critères:");
        JTextField textNbCrit = new JTextField();
        panel.add(labelNbCrit);
        panel.add(textNbCrit);

        JLabel labelNbAlt = new JLabel("Nombre d'alternatives:");
        JTextField textNbAlt = new JTextField();
        panel.add(labelNbAlt);
        panel.add(textNbAlt);

        JButton btnSubmit = new JButton("Submit");
        panel.add(btnSubmit);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int nbcrtr = Integer.parseInt(textNbCrit.getText());
                    int nbAlt = Integer.parseInt(textNbAlt.getText());

                    if (nbcrtr <= 1 || nbAlt <= 1) {
                        JOptionPane.showMessageDialog(frame, "Le nombre de critères et/ou d'alternatives doit être supérieur à 1.");
                        return;
                    }

                    String[] criteres = new String[nbcrtr];
                    double[][] minMax = new double[nbcrtr][2];
                    int[] poids = new int[nbcrtr];
                    String[] alternatives = new String[nbAlt];
                    double[][] evaluations = new double[nbAlt][nbcrtr];
                    double[][] scoresNormalises = new double[nbAlt][nbcrtr];

                    HashSet<String> critereSet = new HashSet<>();
                    int poidsTotal = 0;

                    for (int i = 0; i < nbcrtr; i++) {
                        String critere = JOptionPane.showInputDialog("Nom du critère " + (i + 1) + ":");
                        if (!critereSet.add(critere)) {
                            JOptionPane.showMessageDialog(frame, "Les noms des critères doivent être uniques.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        criteres[i] = critere;
                        minMax[i][0] = Double.parseDouble(JOptionPane.showInputDialog("Valeur minimale pour " + critere + ":"));
                        minMax[i][1] = Double.parseDouble(JOptionPane.showInputDialog("Valeur maximale pour " + critere + ":"));
                        poids[i] = Integer.parseInt(JOptionPane.showInputDialog("Poids pour " + critere + " (1-100):"));
                        poidsTotal += poids[i];
                    }

                    if (poidsTotal != 100) {
                        JOptionPane.showMessageDialog(frame, "La somme des poids des critères doit être égale à 100. Actuellement: " + poidsTotal,
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    HashSet<String> alternativeSet = new HashSet<>();
                    for (int i = 0; i < nbAlt; i++) {
                        String alternative = JOptionPane.showInputDialog("Nom de l'alternative " + (i + 1) + ":");
                        if (!alternativeSet.add(alternative)) {
                            JOptionPane.showMessageDialog(frame, "Les noms des alternatives doivent être uniques.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        alternatives[i] = alternative;
                        for (int j = 0; j < nbcrtr; j++) {
                            evaluations[i][j] = Double.parseDouble(JOptionPane.showInputDialog(
                                    "Valeur réelle pour " + alternative + " selon " + criteres[j] + ":"));
                            scoresNormalises[i][j] = normaliserValeur(evaluations[i][j], minMax[j][0], minMax[j][1]);
                        }
                    }

                    double[] scoresPonderes = new double[nbAlt];
                    for (int i = 0; i < nbAlt; i++) {
                        for (int j = 0; j < nbcrtr; j++) {
                            scoresPonderes[i] += scoresNormalises[i][j] * poids[j];
                        }
                        scoresPonderes[i] /= 100;
                    }

                    Integer[] indices = new Integer[nbAlt];
                    for (int i = 0; i < nbAlt; i++) {
                        indices[i] = i;
                    }

                    Arrays.sort(indices, Comparator.comparingDouble(i -> -scoresPonderes[i]));

                    StringBuilder result = new StringBuilder("Classement des alternatives:\n");
                    for (int i = 0; i < nbAlt; i++) {
                        result.append((i + 1)).append(". ").append(alternatives[indices[i]])
                                .append(" avec un score pondéré de ").append(scoresPonderes[indices[i]]).append("\n");
                    }
                    textArea.setText(result.toString());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Veuillez entrer des nombres valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private double normaliserValeur(double valeur, double min, double max) {
        return (valeur - min) / (max - min) * 9 + 1;
    }

    public static void main(String[] args) {
        new WSM();
    }
}

