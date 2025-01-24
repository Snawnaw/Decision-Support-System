import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

//Etape 6
public class WSM {

    private String problemName;
    private String[] criteres;
    private int[] poids;
    private int[][] contraintes;
    private int nbCrit, nbAlt;
    private String[] alternatives;
    private int[][] scores;

    public WSM() {
        JFrame frame = new JFrame("WSM Evaluation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JLabel labelProblemName = new JLabel("Nom du problème:");
        JTextField textProblemName = new JTextField();
        JButton btnNext = new JButton("Suivant");

        panel.add(labelProblemName);
        panel.add(textProblemName);
        panel.add(btnNext);

        frame.add(panel, BorderLayout.NORTH);
        frame.setVisible(true);

        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                problemName = textProblemName.getText().trim();

                if (problemName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Le nom du problème ne peut pas être vide.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                frame.setVisible(false);
                displayParameterInputFrame();
            }
        });
    }

    private void displayParameterInputFrame() {
        JFrame frame = new JFrame("Gestion des paramètres");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JLabel labelNbCrit = new JLabel("Nombre de critères:");
        JTextField textNbCrit = new JTextField();
        JLabel labelNbAlt = new JLabel("Nombre d'alternatives:");
        JTextField textNbAlt = new JTextField();
        JButton btnSubmitParams = new JButton("Soumettre");

        panel.add(labelNbCrit);
        panel.add(textNbCrit);
        panel.add(labelNbAlt);
        panel.add(textNbAlt);
        panel.add(btnSubmitParams);

        frame.add(panel, BorderLayout.NORTH);
        frame.setVisible(true);

        btnSubmitParams.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    nbCrit = Integer.parseInt(textNbCrit.getText());
                    nbAlt = Integer.parseInt(textNbAlt.getText());

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

                    criteres = new String[nbCrit];
                    poids = new int[nbCrit];
                    contraintes = new int[nbCrit][2];

                    HashSet<String> uniqueCriteres = new HashSet<>();
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
                    }

                    int totalPoids = Arrays.stream(poids).sum();
                    if (totalPoids != 100) {
                        JOptionPane.showMessageDialog(frame,
                                "La somme des poids des critères doit être égale à 100.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    frame.setVisible(false);
                    displayAlternativesInputFrame();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer des valeurs numériques valides.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void displayAlternativesInputFrame() {
        JFrame frame = new JFrame("Saisie des alternatives");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        alternatives = new String[nbAlt];
        scores = new int[nbAlt][nbCrit];

        JLabel labelProblem = new JLabel("Problème: " + problemName);
        panel.add(labelProblem);

        for (int i = 0; i < nbAlt; i++) {
            alternatives[i] = JOptionPane.showInputDialog("Nom de l'alternative " + (i + 1) + ":");

            for (int j = 0; j < nbCrit; j++) {
                int score;
                do {
                    score = Integer.parseInt(JOptionPane.showInputDialog(
                            "Score de " + alternatives[i] + " pour " + criteres[j] +
                                    " (" + contraintes[j][0] + "-" + contraintes[j][1] + "):"));
                } while (score < contraintes[j][0] || score > contraintes[j][1]);
                scores[i][j] = score;
            }
        }

        JButton btnSubmitAlternatives = new JButton("Soumettre les alternatives");
        panel.add(btnSubmitAlternatives);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);

        btnSubmitAlternatives.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double[] scoresPonderes = new double[nbAlt];
                for (int i = 0; i < nbAlt; i++) {
                    for (int j = 0; j < nbCrit; j++) {
                        scoresPonderes[i] += scores[i][j] * poids[j] / 100.0;
                    }
                }

                for (int i = 0; i < scoresPonderes.length; i++) {
                    scoresPonderes[i] = Math.round(scoresPonderes[i] / 100);
                }

                Integer[] indices = new Integer[nbAlt];
                for (int i = 0; i < nbAlt; i++) indices[i] = i;
                Arrays.sort(indices, Comparator.comparingDouble(i -> -scoresPonderes[i]));

                String[] columnRanking = {"Classement", "Alternative", "Score"};
                Object[][] dataRanking = new Object[nbAlt][3];
                for (int i = 0; i < nbAlt; i++) {
                    dataRanking[i][0] = i + 1;
                    dataRanking[i][1] = alternatives[indices[i]];
                    dataRanking[i][2] = String.format("%.3f", scoresPonderes[indices[i]]);
                }

                JTable tableRanking = new JTable(dataRanking, columnRanking);
                JScrollPane scrollRanking = new JScrollPane(tableRanking);

                JPanel resultPanel = new JPanel(new BorderLayout());
                resultPanel.add(scrollRanking, BorderLayout.CENTER);

                JButton btnBackToFirstPage = new JButton("Revenir à la première page");
                btnBackToFirstPage.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.setVisible(false);
                        new WSM();
                    }
                });
                resultPanel.add(btnBackToFirstPage, BorderLayout.SOUTH);

                frame.setContentPane(resultPanel);
                frame.revalidate();
                frame.repaint();
            }
        });
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
