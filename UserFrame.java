package Proiect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Clasa UserFrame reprezintă interfața principală destinată utilizatorilor de rol "user".
 * Aceasta afișează date despre piloți, echipe și curse (GP),
 * precum și posibilitatea de a vota un pilot (Driver of the Day) pentru fiecare cursă,
 * respectiv de a vizualiza cine este Driver of the Day.
 */
public class UserFrame extends JFrame {
    private ArrayList<Driver> drivers;
    private ArrayList<Echipa> echipe;
    private ArrayList<GP> curse;
    private JTable driverTable;
    private JTable echipaTable;
    private JTable curseTable;
    private int currentUserId;

    /**
     * Constructorul clasei UserFrame care inițiază interfața destinată utilizatorilor.
     *
     * @param role   Rolul utilizatorului (în acest caz, "user").
     * @param userId ID-ul utilizatorului conectat.
     */
    public UserFrame(String role, int userId) {
        this.currentUserId = userId;
        setTitle("F1 Viewer");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Setări de culori și fonturi pentru tema modernă roșu-negru
        Color backgroundColor = Color.BLACK;
        Color textColor = Color.WHITE;
        Color accentColor = new Color(231, 76, 60); // roșu vibrant
        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);

        // Încărcarea datelor din baza de date
        drivers = Driver.loadDriversFromDatabase();
        echipe = Echipa.loadTeamsFromDatabase();
        curse = GP.loadRacesFromDatabase();

        // Inițializarea tabelelor
        driverTable = new JTable();
        echipaTable = new JTable();
        curseTable = new JTable();

        // Aplică stilul modern tabelelor
        styleTable(driverTable, accentColor, textColor, font, headerFont);
        styleTable(echipaTable, accentColor, textColor, font, headerFont);
        styleTable(curseTable, accentColor, textColor, font, headerFont);

        // Setează datele în tabele
        initDriverTable();
        initEchipaTable();
        initCurseTable();

        // Crearea tab-urilor
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(headerFont);
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setForeground(textColor);

        // Crearea panourilor stilizate pentru fiecare tab
        JPanel driverPanel = createStyledPanel(backgroundColor);
        driverPanel.add(new JScrollPane(driverTable), BorderLayout.CENTER);

        JPanel echipaPanel = createStyledPanel(backgroundColor);
        echipaPanel.add(new JScrollPane(echipaTable), BorderLayout.CENTER);

        JPanel cursePanel = createStyledPanel(backgroundColor);
        cursePanel.add(new JScrollPane(curseTable), BorderLayout.CENTER);

        // Creare buton pentru votarea "Driver of the Day"
        JButton voteDriverOfTheDayButton = createStyledButton("Votează Driver of the Day", accentColor, textColor, font);
        voteDriverOfTheDayButton.addActionListener(e -> {
            int selectedRaceRow = curseTable.getSelectedRow();
            if (selectedRaceRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectează o cursă pentru a vota!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Pentru simplitate, asociem ID-ul cursei cu indexul rândului selectat
            int selectedRaceId = selectedRaceRow;

            String[] driverNames = drivers.stream().map(Driver::getNume).toArray(String[]::new);
            String selectedDriver = (String) JOptionPane.showInputDialog(
                    this,
                    "Alege un Driver pentru această cursă:",
                    "Votează Driver of the Day",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    driverNames,
                    driverNames[0]
            );

            if (selectedDriver != null) {
                int selectedDriverId = drivers.stream()
                        .filter(driver -> driver.getNume().equals(selectedDriver))
                        .findFirst().get().getId();

                boolean voteSuccess = VoteManager.castVote(selectedRaceId, selectedDriverId, currentUserId);
                if (!voteSuccess) {
                    JOptionPane.showMessageDialog(this, "Nu poți vota de două ori pentru aceeași cursă!", "Vot nereușit", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Vot înregistrat cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        cursePanel.add(voteDriverOfTheDayButton, BorderLayout.SOUTH);

        // Creare buton pentru afișarea "Driver of the Day"
        JButton showDriverOfTheDayButton = createStyledButton("Afișează Driver of the Day", accentColor, textColor, font);
        showDriverOfTheDayButton.addActionListener(e -> {
            int selectedRaceRow = curseTable.getSelectedRow();
            if (selectedRaceRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectează o cursă pentru a vedea rezultatul!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int selectedRaceId = selectedRaceRow;
            String driverOfTheDay = VoteManager.getDriverOfTheDay(selectedRaceId);
            JOptionPane.showMessageDialog(this, "Driver of the Day: " + driverOfTheDay, "Rezultat", JOptionPane.INFORMATION_MESSAGE);
        });
        cursePanel.add(showDriverOfTheDayButton, BorderLayout.NORTH);

        // Creare panou pentru butonul de "Delogare"
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(backgroundColor);
        JButton logoutButton = createStyledButton("Delogare", accentColor, textColor, font);
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutButton);

        // Adăugarea panoului pentru logout în partea de sus a ferestrei
        add(logoutPanel, BorderLayout.NORTH);

        // Adăugarea taburilor în JTabbedPane
        tabbedPane.addTab("Driveri", driverPanel);
        tabbedPane.addTab("Echipe", echipaPanel);
        tabbedPane.addTab("Curse", cursePanel);
        tabbedPane.addTab("Campionatul Driverilor", createSortedDriverTable(accentColor, textColor, font, headerFont, backgroundColor));
        tabbedPane.addTab("Campionatul Echipe", createSortedTeamTable(accentColor, textColor, font, headerFont, backgroundColor));

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Metodă care se ocupă cu gestionarea delogării utilizatorului.
     * Întreabă utilizatorul dacă este sigur, iar dacă acesta confirmă, închide fereastra
     * curentă și deschide fereastra de Login.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Sigur dorești să te deloghezi?",
                "Confirmare Delogare",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Închide fereastra curentă
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    /**
     * Creează un JPanel cu stil personalizat pentru a fi utilizat drept container
     * în interiorul tab-urilor.
     *
     * @param bgColor Culoarea de fundal a panoului.
     * @return Un JPanel stilizat.
     */
    private JPanel createStyledPanel(Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    /**
     * Creează un JButton cu stil personalizat.
     *
     * @param text   Textul afișat pe buton.
     * @param bgColor Culoarea de fundal.
     * @param fgColor Culoarea textului.
     * @param font   Fontul textului.
     * @return Un obiect JButton stilizat.
     */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Font font) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Populează driverTable cu date despre piloți.
     */
    private void initDriverTable() {
        String[] columnNames = {"Nume", "Echipa", "Campionate", "Puncte", "Podiumuri", "Câștiguri"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Driver driver : drivers) {
            Object[] row = {
                    driver.getNume(),
                    driver.getTeamNameFromDB(),
                    driver.getNrCampionate(),
                    driver.getNrPuncte(),
                    driver.getNrPodium(),
                    driver.getNrCastig()
            };
            model.addRow(row);
        }
        driverTable.setModel(model);
    }

    /**
     * Populează echipaTable cu date despre echipe.
     */
    private void initEchipaTable() {
        String[] columnNames = {"Echipa", "Campionate", "Puncte Totale"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Echipa echipa : echipe) {
            Object[] row = {
                    echipa.getNume(),
                    echipa.getNrCampionate(),
                    echipa.getTotalPuncte()
            };
            model.addRow(row);
        }
        echipaTable.setModel(model);
    }

    /**
     * Populează curseTable cu date despre curse (GP).
     */
    private void initCurseTable() {
        String[] columnNames = {"Circuit", "Data", "Distanta", "Momentul din zi", "Sprint"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (GP cursa : curse) {
            Object[] row = {
                    cursa.getNume(),
                    cursa.getDataGP().toString(),
                    cursa.getDistanta(),
                    cursa.getTimeOfDay(),
                    cursa.isSprint() ? "Da" : "Nu"
            };
            model.addRow(row);
        }
        curseTable.setModel(model);
    }

    /**
     * Creează și returnează un JScrollPane care conține un JTable ce afișează
     * clasamentul echipelor (sortat descrescător după puncte).
     *
     * @param accentColor Culoarea de accent (fondul header-ului).
     * @param textColor   Culoarea textului din tabel.
     * @param cellFont    Fontul utilizat pentru celule.
     * @param headerFont  Fontul utilizat pentru antetele coloanelor.
     * @param bgColor     Culoarea de fundal pentru viewport.
     * @return Un JScrollPane cu tabela echipelor sortate.
     */
    private JScrollPane createSortedTeamTable(Color accentColor, Color textColor, Font cellFont, Font headerFont, Color bgColor) {
        String[] columnNames = {"Echipa", "Puncte Totale"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Sortarea echipelor după punctaj descrescător
        echipe.sort(Comparator.comparingInt(Echipa::getTotalPuncte).reversed());
        for (Echipa echipa : echipe) {
            Object[] row = {echipa.getNume(), echipa.getTotalPuncte()};
            model.addRow(row);
        }
        JTable sortedTeamTable = new JTable(model);
        styleTable(sortedTeamTable, accentColor, textColor, cellFont, headerFont);
        JScrollPane scrollPane = new JScrollPane(sortedTeamTable);
        scrollPane.getViewport().setBackground(bgColor);
        return scrollPane;
    }

    /**
     * Creează și returnează un JScrollPane care conține un JTable ce afișează
     * clasamentul piloților (sortat descrescător după puncte).
     *
     * @param accentColor Culoarea de accent (fondul header-ului).
     * @param textColor   Culoarea textului din tabel.
     * @param cellFont    Fontul utilizat pentru celule.
     * @param headerFont  Fontul utilizat pentru antetele coloanelor.
     * @param bgColor     Culoarea de fundal pentru viewport.
     * @return Un JScrollPane cu tabela driverilor sortați.
     */
    private JScrollPane createSortedDriverTable(Color accentColor, Color textColor, Font cellFont, Font headerFont, Color bgColor) {
        String[] columnNames = {"Nume", "Echipa", "Puncte"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Sortarea driverilor după punctaj descrescător
        drivers.sort(Comparator.comparingInt(Driver::getNrPuncte).reversed());
        for (Driver driver : drivers) {
            Object[] row = {driver.getNume(), driver.getTeamNameFromDB(), driver.getNrPuncte()};
            model.addRow(row);
        }
        JTable sortedDriverTable = new JTable(model);
        styleTable(sortedDriverTable, accentColor, textColor, cellFont, headerFont);
        JScrollPane scrollPane = new JScrollPane(sortedDriverTable);
        scrollPane.getViewport().setBackground(bgColor);
        return scrollPane;
    }

    /**
     * Aplică un stil tabelului specificat: fundal, culori, font, înălțimea rândurilor,
     * culoarea grilei și setările pentru antet.
     *
     * @param table      Tabela căreia i se aplică stilul.
     * @param accentColor Culoarea de accent pentru header și grilă.
     * @param textColor   Culoarea textului.
     * @param cellFont    Fontul folosit în celule.
     * @param headerFont  Fontul folosit în antetul tabelului.
     */
    private void styleTable(JTable table, Color accentColor, Color textColor, Font cellFont, Font headerFont) {
        table.setFillsViewportHeight(true);
        table.setBackground(Color.DARK_GRAY);
        table.setForeground(textColor);
        table.setFont(cellFont);
        table.setRowHeight(25);
        table.setGridColor(accentColor);

        JTableHeader header = table.getTableHeader();
        header.setBackground(accentColor);
        header.setForeground(textColor);
        header.setFont(headerFont);
        header.setReorderingAllowed(false);
    }
}


