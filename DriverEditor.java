package Proiect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa reprezintă un editor (dialog) pentru modificarea informațiilor despre un pilot (Driver).
 * Permite utilizatorului să schimbe numele pilotului, echipa, campionatele, punctele, podiumurile
 * și victoriile, și actualizează apoi datele în baza de date.
 */
public class DriverEditor extends JDialog {
    private Driver driver;
    private JTextField nameField;
    private JTextField teamField;
    private JTextField championshipsField;
    private JTextField pointsField;
    private JTextField podiumsField;
    private JTextField winsField;
    private List<Echipa> echipe;

    /**
     * Constructorul pentru fereastra de editare a unui pilot.
     *
     * @param parent  Fereastra părinte (Frame) a acestui dialog.
     * @param driver  Obiectul Driver care urmează să fie editat.
     * @param echipe  Lista echipelor disponibile în aplicație.
     */
    public DriverEditor(Frame parent, Driver driver, List<Echipa> echipe) {
        super(parent, "Editare Driver", true);
        this.driver = driver;
        this.echipe = (echipe != null) ? echipe : new ArrayList<>();

        Color backgroundColor = Color.BLACK;
        Color panelBackground = new Color(40, 40, 40);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(231, 76, 60);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        nameField = createStyledTextField(driver.getNume(), fieldFont, textColor);
        teamField = createStyledTextField(driver.getTeamNameFromDB(), fieldFont, textColor);
        championshipsField = createStyledTextField(String.valueOf(driver.getNrCampionate()), fieldFont, textColor);
        pointsField = createStyledTextField(String.valueOf(driver.getNrPuncte()), fieldFont, textColor);
        podiumsField = createStyledTextField(String.valueOf(driver.getNrPodium()), fieldFont, textColor);
        winsField = createStyledTextField(String.valueOf(driver.getNrCastig()), fieldFont, textColor);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBackground(panelBackground);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(createStyledLabel("Nume:", labelFont, textColor));
        panel.add(nameField);

        panel.add(createStyledLabel("Echipa:", labelFont, textColor));
        panel.add(teamField);

        panel.add(createStyledLabel("Campionate:", labelFont, textColor));
        panel.add(championshipsField);

        panel.add(createStyledLabel("Puncte:", labelFont, textColor));
        panel.add(pointsField);

        panel.add(createStyledLabel("Podiumuri:", labelFont, textColor));
        panel.add(podiumsField);

        panel.add(createStyledLabel("Victorii:", labelFont, textColor));
        panel.add(winsField);

        JButton saveButton = new JButton("Salvează");
        saveButton.setFont(buttonFont);
        saveButton.setBackground(accentColor);
        saveButton.setForeground(textColor);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acțiune buton "Salvează"
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Setează valorile din câmpurile text în obiectul Driver
                    driver.setNume(nameField.getText());
                    driver.setNrCampionate(Integer.parseInt(championshipsField.getText()));
                    driver.setNrPuncte(Integer.parseInt(pointsField.getText()));
                    driver.setNrPodium(Integer.parseInt(podiumsField.getText()));
                    driver.setNrCastig(Integer.parseInt(winsField.getText()));

                    // Verifică dacă echipa introdusă există în lista de echipe
                    String newTeamName = teamField.getText();
                    Echipa foundEchipa = findEchipaByName(newTeamName);

                    if (foundEchipa != null) {
                        // Asociază ID-ul echipei în cazul în care există
                        driver.setTeamId(foundEchipa.getId());
                    } else {
                        // Afișează mesaj de avertizare dacă echipa nu există
                        JOptionPane.showMessageDialog(
                                DriverEditor.this,
                                "Echipa introdusă nu există în listă. Team ID rămâne neschimbat.",
                                "Eroare echipă",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                    // Actualizează totalul de puncte al echipei, dacă există
                    updateTeamPoints(newTeamName);

                    // Salvează modificările în baza de date
                    DatabaseConnection.updateDriver(
                            driver.getId(),
                            driver.getNume(),
                            driver.getTeamId(),
                            driver.getNrCampionate(),
                            driver.getNrPuncte(),
                            driver.getNrPodium(),
                            driver.getNrCastig()
                    );

                    // Închide dialogul după salvare
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            DriverEditor.this,
                            "Introduceți valori numerice valide pentru câmpurile numerice.",
                            "Eroare",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(panelBackground);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        buttonPanel.add(saveButton);

        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Creează și returnează un JLabel cu stil personalizat.
     *
     * @param text      Textul etichetei.
     * @param font      Fontul textului.
     * @param textColor Culoarea textului.
     * @return Un JLabel stilizat.
     */
    private JLabel createStyledLabel(String text, Font font, Color textColor) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(textColor);
        return label;
    }

    /**
     * Creează și returnează un JTextField cu stil personalizat și text inițial.
     *
     * @param text      Textul inițial pentru câmpul de text.
     * @param font      Fontul folosit pentru text.
     * @param textColor Culoarea textului și a cursorului.
     * @return Un JTextField stilizat.
     */
    private JTextField createStyledTextField(String text, Font font, Color textColor) {
        JTextField textField = new JTextField(text);
        textField.setFont(font);
        textField.setForeground(textColor);
        textField.setBackground(new Color(64, 64, 64));
        textField.setCaretColor(textColor);
        textField.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));
        return textField;
    }

    /**
     * Caută în lista de echipe echipa care are numele specificat.
     *
     * @param teamName Numele echipei căutate.
     * @return Obiectul Echipa găsit sau null dacă nu există o echipă cu acest nume.
     */
    private Echipa findEchipaByName(String teamName) {
        for (Echipa e : echipe) {
            if (e.getNume().equalsIgnoreCase(teamName)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Actualizează punctajul total al echipei pe baza sumelor de puncte ale driverilor din acea echipă.
     *
     * @param teamName Numele echipei pentru care se actualizează punctajul.
     */
    private void updateTeamPoints(String teamName) {
        Echipa echipa = findEchipaByName(teamName);
        if (echipa != null) {
            int totalPoints = 0;
            List<Driver> driversForTeam = echipa.getDriversForTeam();
            for (Driver d : driversForTeam) {
                totalPoints += d.getNrPuncte();
            }
            echipa.setTotalPoints(totalPoints);

            // Actualizează și în baza de date
            DatabaseConnection.updateTeam(echipa.getId(), echipa.getNume(), totalPoints, echipa.getNrCampionate());
        }
    }
}

