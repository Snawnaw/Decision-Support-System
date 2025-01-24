import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Comparator;

//Etape 7
public class WSM {

    private String[] criteres;
    private int[] poids;
    private String[] alternatives;
    private int[][] scores;
    private int[][] contraintes;
    private String problemName;

    public WSM() {
        JFrame frame = new JFrame("WSM Evaluation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel labelProblemName = new JLabel("Nom du problème:");
        JTextField textProblemName = new JTextField();
        JLabel labelNbCrit = new JLabel("Nombre de critères:");
        JTextField textNbCrit = new JTextField();
        JLabel labelNbAlt = new JLabel("Nombre d'alternatives:");
        JTextField textNbAlt = new JTextField();

        JButton btnSubmit = new JButton("Valider");
        JButton btnSave = new JButton("Sauvegarder");
        JButton btnLoad = new JButton("Charger");
        JButton btnExport = new JButton("Exporter Résultats");
        JButton btnRestart = new JButton("Revenir à la page initiale");

        inputPanel.add(labelProblemName);
        inputPanel.add(textProblemName);
        inputPanel.add(labelNbCrit);
        inputPanel.add(textNbCrit);
        inputPanel.add(labelNbAlt);
        inputPanel.add(textNbAlt);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnRestart);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Alternative", "Critère", "Score"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);

        btnSubmit.addActionListener(e -> {
            try {
                problemName = textProblemName.getText();
                if (problemName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Le nom du problème ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int nbCrit = Integer.parseInt(textNbCrit.getText());
                int nbAlt = Integer.parseInt(textNbAlt.getText());

                if (nbCrit <= 1 || nbAlt <= 1) {
                    JOptionPane.showMessageDialog(frame, "Veuillez entrer un nombre valide de critères et alternatives.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                criteres = new String[nbCrit];
                poids = new int[nbCrit];
                alternatives = new String[nbAlt];
                scores = new int[nbAlt][nbCrit];
                contraintes = new int[nbCrit][2];

                for (int i = 0; i < nbCrit; i++) {
                    criteres[i] = JOptionPane.showInputDialog("Nom du critère " + (i + 1) + ":");
                    String[] contrainte = JOptionPane.showInputDialog("Contrainte pour " + criteres[i] + " (format min-max):").split("-");
                    contraintes[i][0] = Integer.parseInt(contrainte[0]);
                    contraintes[i][1] = Integer.parseInt(contrainte[1]);
                    poids[i] = Integer.parseInt(JOptionPane.showInputDialog("Poids du critère " + criteres[i] + " (1-100):"));
                }

                int totalPoids = Arrays.stream(poids).sum();
                if (totalPoids != 100) {
                    JOptionPane.showMessageDialog(frame, "La somme des poids doit être égale à 100.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Set<String> alternativeSet = new HashSet<>();
                for (int i = 0; i < nbAlt; i++) {
                    String alternativeName;
                    do {
                        alternativeName = JOptionPane.showInputDialog("Nom de l'alternative " + (i + 1) + ":");
                        if (alternativeSet.contains(alternativeName)) {
                            JOptionPane.showMessageDialog(frame, "Le nom de l'alternative doit être unique.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (alternativeSet.contains(alternativeName));

                    alternativeSet.add(alternativeName);
                    alternatives[i] = alternativeName;
                    for (int j = 0; j < nbCrit; j++) {
                        int score;
                        do {
                            score = Integer.parseInt(JOptionPane.showInputDialog("Score de " + alternatives[i] + " pour " + criteres[j] + " (" + contraintes[j][0] + "-" + contraintes[j][1] + "):"));
                        } while (score < contraintes[j][0] || score > contraintes[j][1]);
                        scores[i][j] = score;
                        tableModel.addRow(new Object[]{alternatives[i], criteres[j], scores[i][j]});
                    }
                }

                afficherClassement(tableModel);
                JOptionPane.showMessageDialog(frame, "Données saisies avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRestart.addActionListener(e -> {
            frame.dispose();
            new WSM();
        });

        btnSave.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int choice = fileChooser.showSaveDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                        oos.writeObject(problemName);
                        oos.writeObject(criteres);
                        oos.writeObject(poids);
                        oos.writeObject(alternatives);
                        oos.writeObject(scores);
                        oos.writeObject(contraintes);
                        JOptionPane.showMessageDialog(frame, "Données sauvegardées avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la sauvegarde : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLoad.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int choice = fileChooser.showOpenDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        problemName = (String) ois.readObject();
                        criteres = (String[]) ois.readObject();
                        poids = (int[]) ois.readObject();
                        alternatives = (String[]) ois.readObject();
                        scores = (int[][]) ois.readObject();
                        contraintes = (int[][]) ois.readObject();
                        JOptionPane.showMessageDialog(frame, "Données chargées avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        afficherClassement(tableModel);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors du chargement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExport.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int choice = fileChooser.showSaveDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write("Nom du problème: " + problemName + "\n");
                        writer.write("Critères:\n");
                        for (int i = 0; i < criteres.length; i++) {
                            writer.write(criteres[i] + " (Poids: " + poids[i] + ")\n");
                        }
                        writer.write("Alternatives et Scores:\n");
                        for (int i = 0; i < alternatives.length; i++) {
                            writer.write(alternatives[i] + ":\n");
                            for (int j = 0; j < criteres.length; j++) {
                                writer.write("  " + criteres[j] + ": " + scores[i][j] + "\n");
                            }
                        }
                        JOptionPane.showMessageDialog(frame, "Résultats exportés avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'exportation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private void afficherClassement(DefaultTableModel tableModel) {
        int[] totalScores = new int[alternatives.length];
        for (int i = 0; i < alternatives.length; i++) {
            totalScores[i] = 0;
            for (int j = 0; j < criteres.length; j++) {
                totalScores[i] += scores[i][j] * poids[j];
            }
            totalScores[i] = totalScores[i] / 100;
        }

        Integer[] indexes = new Integer[alternatives.length];
        for (int i = 0; i < alternatives.length; i++) {
            indexes[i] = i;
        }

        Arrays.sort(indexes, Comparator.comparingInt(i -> -totalScores[i]));
        tableModel.setRowCount(0);
        for (int i = 0; i < alternatives.length; i++) {
            int idx = indexes[i];
            tableModel.addRow(new Object[]{alternatives[idx], "Score Total", totalScores[idx]});
        }
    }

    public static void main(String[] args) {
        new WSM();
    }
}
