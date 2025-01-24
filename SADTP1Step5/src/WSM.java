import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

//Etape 5
public class WSM {

    public WSM() {
        JFrame frame = new JFrame("WSM Evaluation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JLabel labelNbCrit = new JLabel("Nombre de critères:");
        JTextField textNbCrit = new JTextField();
        JLabel labelNbAlt = new JLabel("Nombre d'alternatives:");
        JTextField textNbAlt = new JTextField();
        JButton btnSubmit = new JButton("Submit");

        panel.add(labelNbCrit);
        panel.add(textNbCrit);
        panel.add(labelNbAlt);
        panel.add(textNbAlt);
        panel.add(btnSubmit);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int nbCrit = Integer.parseInt(textNbCrit.getText());
                    int nbAlt = Integer.parseInt(textNbAlt.getText());

                    if (nbCrit <= 1) {
                        JOptionPane.showMessageDialog(frame,
                                "Le nombre de critères doit être supérieur à 1.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (nbAlt <= 1) {
                        JOptionPane.showMessageDialog(frame,
                                "Le nombre d'alternatives doit être supérieur à 1.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] criteres = new String[nbCrit];
                    int[] poids = new int[nbCrit];
                    String[] alternatives = new String[nbAlt];
                    int[][] scores = new int[nbAlt][nbCrit];
                    int[][] contraintes = new int[nbCrit][2];

                    HashSet<String> uniqueCriteres = new HashSet<>();
                    int poidsTotal = 0;

                    for (int i = 0; i < nbCrit; i++) {
                        String critere;
                        do {
                            critere = JOptionPane.showInputDialog("Nom du critère " + (i + 1) + ":");
                            if (uniqueCriteres.contains(critere)) {
                                JOptionPane.showMessageDialog(frame,
                                        "Le nom du critère doit être unique.",
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } while (uniqueCriteres.contains(critere));

                        uniqueCriteres.add(critere);
                        criteres[i] = critere;

                        String[] contrainte;
                        do {
                            contrainte = JOptionPane.showInputDialog(
                                    "Contrainte pour " + criteres[i] + " (format min-max):").split("-");
                            if (contrainte.length != 2 || !isValidInteger(contrainte[0]) || !isValidInteger(contrainte[1])) {
                                JOptionPane.showMessageDialog(frame,
                                        "Veuillez entrer des contraintes valides (format min-max).",
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                                contrainte = null;
                            }
                        } while (contrainte == null);

                        contraintes[i][0] = Integer.parseInt(contrainte[0]);
                        contraintes[i][1] = Integer.parseInt(contrainte[1]);

                        int poidsCritere;
                        do {
                            try {
                                poidsCritere = Integer.parseInt(JOptionPane.showInputDialog(
                                        "Poids du critère " + criteres[i] + " (1-100):"));
                                if (poidsCritere < 1 || poidsCritere > 100) {
                                    JOptionPane.showMessageDialog(frame,
                                            "Le poids doit être entre 1 et 100.",
                                            "Erreur",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(frame,
                                        "Veuillez entrer un poids valide.",
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } while (true);

                        poids[i] = poidsCritere;
                        poidsTotal += poidsCritere;
                    }

                    if (poidsTotal != 100) {
                        JOptionPane.showMessageDialog(frame,
                                "La somme des poids doit être égale à 100.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    HashSet<String> uniqueAlternatives = new HashSet<>();
                    for (int i = 0; i < nbAlt; i++) {
                        String alternative;
                        do {
                            alternative = JOptionPane.showInputDialog("Nom de l'alternative " + (i + 1) + ":");
                            if (uniqueAlternatives.contains(alternative)) {
                                JOptionPane.showMessageDialog(frame,
                                        "Le nom de l'alternative doit être unique.",
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } while (uniqueAlternatives.contains(alternative));

                        uniqueAlternatives.add(alternative);
                        alternatives[i] = alternative;

                        for (int j = 0; j < nbCrit; j++) {
                            int score;
                            do {
                                score = Integer.parseInt(JOptionPane.showInputDialog(
                                        "Score de " + alternatives[i] + " pour " + criteres[j] +
                                                " (" + contraintes[j][0] + "-" + contraintes[j][1] + "):"));
                                if (score < contraintes[j][0] || score > contraintes[j][1]) {
                                    JOptionPane.showMessageDialog(frame,
                                            "Le score doit être entre " + contraintes[j][0] + " et " + contraintes[j][1] + ".",
                                            "Erreur",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    break;
                                }
                            } while (true);

                            scores[i][j] = score;
                        }
                    }

                    double[] scoresPonderes = new double[nbAlt];
                    for (int i = 0; i < nbAlt; i++) {
                        for (int j = 0; j < nbCrit; j++) {
                            scoresPonderes[i] += scores[i][j] * poids[j] / 100.0;
                        }
                    }

                    double minScore = Arrays.stream(scoresPonderes).min().orElse(0);
                    double maxScore = Arrays.stream(scoresPonderes).max().orElse(1);
                    for (int i = 0; i < nbAlt; i++) {
                        scoresPonderes[i] = 1.0 + (scoresPonderes[i] - minScore) * (10.0 - 1.0) / (maxScore - minScore);
                    }

                    Integer[] indices = new Integer[nbAlt];
                    for (int i = 0; i < nbAlt; i++) indices[i] = i;
                    Arrays.sort(indices, Comparator.comparingDouble(i -> -scoresPonderes[i]));

                    StringBuilder result = new StringBuilder("Classement des alternatives:\n");
                    for (int i = 0; i < nbAlt; i++) {
                        result.append((i + 1) + ". " + alternatives[indices[i]]
                                + " avec un score pondéré de " + scoresPonderes[indices[i]] + "\n");
                    }
                    result.append("La meilleure alternative est: " + alternatives[indices[0]]);
                    textArea.setText(result.toString());

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer des valeurs numériques valides.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Erreur: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static void main(String[] args) {
        new WSM();
    }
}
