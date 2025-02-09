package Proiect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clasa {@code RaceResultsDialog} reprezintă un dialog ce permite selectarea piloților
 * de pe primele 10 poziții la finalul unei curse, atribuindu-le puncte corespunzătoare.
 */
public class RaceResultsDialog extends JDialog {

    private JComboBox<String>[] positionComboBoxes;
    private JButton confirmButton;
    private ArrayList<Driver> drivers;

    /**
     * Array-ul cu punctele corespunzătoare primelor 10 poziții (în format FIA clasic).
     */
    private final int[] POINTS = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};

    /**
     * Returnează array-ul de {@code JComboBox<String>} pentru pozițiile selectate.
     *
     * @return Array de combo box-uri pentru poziții.
     */
    public JComboBox<String>[] getPositionComboBoxes() {
        return positionComboBoxes;
    }

    /**
     * Constructor pentru crearea dialogului care afișează piloții și le permite
     * atribuire de puncte în funcție de poziția în cursă.
     *
     * @param owner      Fereastra părinte a dialogului (de tip {@code Frame}).
     * @param selectedGP Cursa selectată (obiect {@code GP}).
     * @param drivers    Lista de piloți disponibili.
     */
    public RaceResultsDialog(Frame owner, GP selectedGP, ArrayList<Driver> drivers) {
        super(owner, "Rezultate Cursa: " + selectedGP.getNume(), true);
        this.drivers = new ArrayList<>(drivers);

        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(owner);

        JLabel titleLabel = new JLabel("Selectează Driverii pentru Cursa " + selectedGP.getNume());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JPanel positionsPanel = new JPanel();
        positionsPanel.setLayout(new GridLayout(11, 2, 10, 10));

        // Cream combo box-uri pentru pozițiile 1-10
        positionComboBoxes = new JComboBox[10];

        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel("Poziția " + (i + 1) + " (" + POINTS[i] + " pct):");
            positionComboBoxes[i] = new JComboBox<>();
            for (Driver driver : drivers) {
                positionComboBoxes[i].addItem(driver.getNume());
            }
            positionsPanel.add(label);
            positionsPanel.add(positionComboBoxes[i]);
        }

        add(positionsPanel, BorderLayout.CENTER);

        confirmButton = new JButton("Confirmă Rezultatele");
        confirmButton.addActionListener(e -> assignPoints());
        add(confirmButton, BorderLayout.SOUTH);
    }

    /**
     * Metodă care preia valorile selectate în combo box-uri și asignează punctele
     * corespunzătoare fiecărui pilot, fără a permite duplicarea piloților.
     */
    void assignPoints() {
        ArrayList<String> selectedDrivers = new ArrayList<>();

        // Parcurgem pozițiile, atribuind puncte și evitând selectarea dublă a unui pilot
        for (int i = 0; i < positionComboBoxes.length; i++) {
            String selectedDriverName = (String) positionComboBoxes[i].getSelectedItem();

            if (selectedDriverName != null && !selectedDrivers.contains(selectedDriverName)) {
                selectedDrivers.add(selectedDriverName);
                for (Driver driver : drivers) {
                    if (driver.getNume().equals(selectedDriverName)) {
                        driver.setNrPuncte(driver.getNrPuncte() + POINTS[i]);
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Punctele au fost asignate!", "Succes", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

