package Proiect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa {@code RaceManager} reprezintă un dialog prin care un administrator
 * poate gestiona cursele (Grand Prix - GP). Acest dialog oferă funcționalități
 * de adăugare, ștergere și resetare a punctelor din baza de date.
 */
public class RaceManager extends JDialog {
    private JTable raceTable;
    private DefaultTableModel tableModel;
    private List<GP> curse;

    /**
     * Constructorul clasei {@code RaceManager}.
     *
     * @param parent Fereastra (Frame) părinte a acestui dialog.
     */
    public RaceManager(Frame parent) {
        super(parent, "Administrare Curse (Doar admin)", true);

        // Setări de culori și fonturi pentru tema modernă roșu-negru
        Color backgroundColor = Color.BLACK;
        Color panelBackground = new Color(40, 40, 40);  // negru ușor mai deschis
        Color textColor = Color.WHITE;
        Color accentColor = new Color(231, 76, 60);    // roșu vibrant
        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);

        // Setare fundal general și layout-ul ferestrei
        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500); // Dimensiune mărită
        setLocationRelativeTo(parent);

        // Încărcare date din DB
        curse = loadRacesFromDatabase();

        // Configurare tabel
        String[] columnNames = {"ID", "Nume", "Data", "Distanta (km)", "Time of Day", "Sprint"};
        tableModel = new DefaultTableModel(columnNames, 0);
        raceTable = new JTable(tableModel);
        styleTable(raceTable, accentColor, textColor, font, headerFont);
        refreshTable(); // Populează tabelul cu date

        JScrollPane scrollPane = new JScrollPane(raceTable);
        scrollPane.getViewport().setBackground(panelBackground);
        add(scrollPane, BorderLayout.CENTER);

        // Panou pentru butoane (GridLayout: 1 rând, 3 coloane, gap de 10px)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(panelBackground);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Buton "Adaugă Cursă și Rezultate"
        JButton addButton = createStyledButton("Adaugă Cursă și Rezultate", accentColor, textColor, font);
        addButton.addActionListener(e -> addRaceAndResults());
        buttonPanel.add(addButton);

        // Buton "Șterge Cursă"
        JButton removeButton = createStyledButton("Șterge Cursă", accentColor, textColor, font);
        removeButton.addActionListener(e -> removeRace());
        buttonPanel.add(removeButton);

        // Buton "Resetează Punctele Driverilor"
        JButton clearPointsButton = createStyledButton("Resetează Punctele Driverilor", accentColor, textColor, font);
        clearPointsButton.addActionListener(e -> clearDriverPoints());
        buttonPanel.add(clearPointsButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Metodă pentru stilizarea unui tabel (JTable).
     *
     * @param table      Tabelul care va fi stilizat.
     * @param accentColor Culoarea de accent (folosită pentru header și grilă).
     * @param textColor   Culoarea textului din tabel.
     * @param cellFont    Fontul pentru celule.
     * @param headerFont  Fontul pentru antetele coloanelor.
     */
    private void styleTable(JTable table, Color accentColor, Color textColor, Font cellFont, Font headerFont) {
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(64, 64, 64));
        table.setForeground(textColor);
        table.setFont(cellFont);
        table.setRowHeight(25);
        table.setGridColor(accentColor);
        table.setSelectionBackground(new Color(173, 216, 230));

        JTableHeader header = table.getTableHeader();
        header.setBackground(accentColor);
        header.setForeground(textColor);
        header.setFont(headerFont);
        header.setReorderingAllowed(false);
    }

    /**
     * Metodă pentru crearea unui buton cu stil personalizat.
     *
     * @param text   Textul butonului.
     * @param bgColor Culoarea de fundal.
     * @param fgColor Culoarea textului.
     * @param font    Fontul textului.
     * @return Un obiect JButton stilizat.
     */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Font font) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Încarcă toate cursele din baza de date și returnează o listă de obiecte GP.
     *
     * @return Listă de obiecte GP.
     */
    private List<GP> loadRacesFromDatabase() {
        List<GP> list = new ArrayList<>();
        String sql = "SELECT * FROM GP ORDER BY dataGP";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GP gp = new GP(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getInt("distanta"),
                        LocalDate.parse(rs.getString("dataGP")),
                        rs.getString("timeOfDay"),
                        rs.getBoolean("isSprint")
                );
                list.add(gp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea datelor din DB!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * Metodă pentru reîmprospătarea (reafișarea) datelor în tabelul de curse.
     * Resetează conținutul modelului și inserează datele actuale din {@code curse}.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (GP gp : curse) {
            Object[] row = {
                    gp.getId(),
                    gp.getNume(),
                    gp.getDataGP().toString(),
                    gp.getDistanta(),
                    gp.getTimeOfDay(),
                    gp.isSprint() ? "Da" : "Nu"
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Deschide un dialog pentru adăugarea unei noi curse, apoi permite inserarea
     * rezultatelor (locurile 1-10 și fastest lap).
     */
    private void addRaceAndResults() {
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField distField = new JTextField();
        JTextField timeOfDayField = new JTextField();
        JCheckBox sprintCheckBox = new JCheckBox("Este cursă Sprint?");
        sprintCheckBox.setBackground(new Color(40, 40, 40));
        sprintCheckBox.setForeground(Color.WHITE);
        sprintCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        Object[] message = {
                "Nume cursă:", nameField,
                "Data (YYYY-MM-DD):", dateField,
                "Distanta (km):", distField,
                "Time of Day:", timeOfDayField,
                sprintCheckBox
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Adaugă o cursă nouă",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String nume = nameField.getText().trim();
            String dataStr = dateField.getText().trim();
            String distStr = distField.getText().trim();
            String timeOfDay = timeOfDayField.getText().trim();
            boolean isSprint = sprintCheckBox.isSelected();

            if (nume.isEmpty() || dataStr.isEmpty() || distStr.isEmpty() || timeOfDay.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completează toate câmpurile!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int distanta = Integer.parseInt(distStr);
                LocalDate dataGp = LocalDate.parse(dataStr);

                // Inserează cursa în baza de date și obține noul ID
                int newId = insertRaceIntoDB(nume, dataGp, distanta, timeOfDay, isSprint);

                // Pregătește câmpurile pentru introducerea rezultatelor
                JTextField[] driverFields = new JTextField[10];
                for (int i = 0; i < 10; i++) {
                    driverFields[i] = new JTextField();
                }
                JTextField fastestLapField = new JTextField();

                // Mesaj compus din label-uri și câmpuri pentru primele 10 poziții + fastest lap
                Object[] resultsMessage = new Object[22];
                for (int i = 0; i < 10; i++) {
                    resultsMessage[i * 2] = "Locul " + (i + 1) + " - ID Driver:";
                    resultsMessage[i * 2 + 1] = driverFields[i];
                }
                resultsMessage[20] = "Fastest Lap - ID Driver:";
                resultsMessage[21] = fastestLapField;

                int resultsOption = JOptionPane.showConfirmDialog(
                        this,
                        resultsMessage,
                        "Adaugă Rezultatele Curse",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (resultsOption == JOptionPane.OK_OPTION) {
                    // Atribuire de puncte pentru locurile 1-10
                    for (int i = 0; i < 10; i++) {
                        String driverIdStr = driverFields[i].getText().trim();
                        if (!driverIdStr.isEmpty()) {
                            int driverId = Integer.parseInt(driverIdStr);
                            updateDriverPoints(driverId, i + 1, false);
                        }
                    }

                    // Atribuire de puncte pentru fastest lap
                    String fastestLapDriverIdStr = fastestLapField.getText().trim();
                    if (!fastestLapDriverIdStr.isEmpty()) {
                        int fastestLapDriverId = Integer.parseInt(fastestLapDriverIdStr);
                        updateDriverPoints(fastestLapDriverId, 0, true);
                    }

                    // Actualizează punctele echipelor și reîmprospătează datele
                    updateTeamPoints();
                    refreshAllTables();
                }

                // Adaugă cursa nouă în listă și reafișează în tabel
                GP gp = new GP(newId, nume, distanta, dataGp, timeOfDay, isSprint);
                curse.add(gp);
                refreshTable();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduceți valori numerice valide pentru câmpurile numerice.", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Eroare la conversia datei/distantei sau la DB!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Actualizează toate tabelele din interfață după ce se adaugă/șterge/editează
     * o cursă sau se modifică puncte.
     */
    private void refreshAllTables() {
        refreshTable();
        updateTeamPoints();
        loadRacesFromDatabase();
        JOptionPane.showMessageDialog(this, "Datele au fost actualizate cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Actualizează punctele echipelor în baza de date, însumând punctele tuturor driverilor
     * care aparțin fiecărei echipe.
     */
    private void updateTeamPoints() {
        String sql = "UPDATE Echipe SET totalPuncte = (SELECT SUM(nr_puncte) FROM Drivers WHERE Drivers.team_id = Echipe.id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la actualizarea punctelor echipelor!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Șterge cursa selectată din baza de date, după ce confirmă intenția de ștergere.
     */
    private void removeRace() {
        int selectedRow = raceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectează o cursă pentru a o șterge!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int raceId = (int) raceTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ești sigur că vrei să ștergi această cursă?",
                "Confirmare Ștergere",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM GP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, raceId);
            pstmt.executeUpdate();

            curse.removeIf(gp -> gp.getId() == raceId);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Cursa a fost ștearsă cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la ștergerea cursei din DB!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inserează o nouă cursă în baza de date și returnează ID-ul generat pentru aceasta.
     *
     * @param nume      Numele cursei.
     * @param data      Data cursei (tip LocalDate).
     * @param distanta  Distanța cursei.
     * @param timeOfDay Momentul zilei (ex. "Day", "Night").
     * @param isSprint  Indică dacă este cursă Sprint.
     * @return ID-ul (generat automat) al noii curse inserate în baza de date.
     * @throws SQLException Dacă apare o eroare la inserarea în baza de date.
     */
    private int insertRaceIntoDB(String nume, LocalDate data, int distanta, String timeOfDay, boolean isSprint) throws SQLException {
        int generatedId = -1;
        String sql = "INSERT INTO GP (nume, dataGP, distanta, timeOfDay, isSprint) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nume);
            pstmt.setString(2, data.toString());
            pstmt.setInt(3, distanta);
            pstmt.setString(4, timeOfDay);
            pstmt.setBoolean(5, isSprint);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        }
        return generatedId;
    }

    /**
     * Actualizează punctele unui driver pe baza poziției ocupate și a eventualei posesii
     * a celui mai rapid tur (fastest lap).
     *
     * @param driverId     ID-ul driverului ce urmează să fie actualizat.
     * @param position     Poziția ocupată (1-10). 0 se folosește pentru fastest lap.
     * @param isFastestLap Indică dacă pilotul a avut cel mai rapid tur.
     */
    private void updateDriverPoints(int driverId, int position, boolean isFastestLap) {
        // Punctele acordate pentru primele 10 locuri (FIA standard)
        int[] points = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};
        int pointsAwarded = (position > 0 && position <= 10) ? points[position - 1] : 0;

        // Punct suplimentar pentru fastest lap
        if (isFastestLap) {
            pointsAwarded += 1;
        }

        // Creștem numărul de podium și numărul de victorii dacă e cazul
        int isPodium = (position == 1 || position == 2 || position == 3) ? 1 : 0;
        int isWinner = (position == 1) ? 1 : 0;

        String sql = "UPDATE Drivers SET nr_puncte = nr_puncte + ?, nr_podium = nr_podium + ?, nr_castig = nr_castig + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pointsAwarded);
            pstmt.setInt(2, isPodium);
            pstmt.setInt(3, isWinner);
            pstmt.setInt(4, driverId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la actualizarea punctelor driverului în DB!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resetează punctele tuturor driverilor din baza de date la zero, resetând
     * și numărul de podiumuri și de victorii.
     */
    private void clearDriverPoints() {
        // SQL pentru resetarea punctelor driverilor
        String resetDriverPointsSql = "UPDATE Drivers SET nr_puncte = 0, nr_podium = 0, nr_castig = 0";
        // SQL pentru resetarea punctelor echipelor
        String resetTeamPointsSql = "UPDATE Echipe SET totalPuncte = 0";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Resetarea punctelor driverilor
            try (PreparedStatement pstmtDrivers = conn.prepareStatement(resetDriverPointsSql)) {
                pstmtDrivers.executeUpdate();
            }

            // Resetarea punctelor echipelor
            try (PreparedStatement pstmtTeams = conn.prepareStatement(resetTeamPointsSql)) {
                pstmtTeams.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Toate punctele driverilor și echipelor au fost resetate!", "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la resetarea punctelor!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

}


