package Proiect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Clasa MainFrame reprezintă interfața principală a aplicației pentru utilizatorii
 * cu rol de administrator. Afișează tabele cu driveri, echipe și curse, și oferă funcționalități
 * de editare a acestora.
 */
public class MainFrame extends JFrame {
    private ArrayList<Driver> drivers;
    private ArrayList<Echipa> echipe;
    private ArrayList<GP> curse;
    private JTable driverTable;
    private JTable echipaTable;
    private JTable curseTable;
    private String role;
    private JTabbedPane tabbedPane;

    /**
     * Constructorul clasei MainFrame. Creează interfața principală și inițializează
     * componentele UI în funcție de rolul utilizatorului (admin sau user).
     *
     * @param role Rolul utilizatorului (poate fi "admin" sau altceva).
     */
    public MainFrame(String role) {
        this.role = role;
        setTitle("F1 Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        Color backgroundColor = Color.BLACK;
        Color textColor = Color.WHITE;
        Color accentColor = new Color(231, 76, 60);
        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);

        // Încărcarea datelor din baza de date
        loadData();

        // Inițializarea tabelelor
        driverTable = new JTable();
        echipaTable = new JTable();
        curseTable = new JTable();

        // Aplicarea stilului la tabele
        styleTable(driverTable, accentColor, textColor, font, headerFont);
        styleTable(echipaTable, accentColor, textColor, font, headerFont);
        styleTable(curseTable, accentColor, textColor, font, headerFont);

        // Popularea tabelelor
        initDriverTable();
        initEchipaTable();
        initCurseTable();

        // Configurarea JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(headerFont);
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setForeground(textColor);

        // Crearea panourilor
        JPanel driverPanel = createStyledPanel(backgroundColor);
        driverPanel.add(new JScrollPane(driverTable), BorderLayout.CENTER);

        JPanel echipaPanel = createStyledPanel(backgroundColor);
        echipaPanel.add(new JScrollPane(echipaTable), BorderLayout.CENTER);

        JPanel cursePanel = createStyledPanel(backgroundColor);
        cursePanel.add(new JScrollPane(curseTable), BorderLayout.CENTER);

        // Butoane și funcționalități specifice administratorului
        if (role.equalsIgnoreCase("admin")) {
            JButton manageRacesButton = createStyledButton("Administrează Curse", accentColor, textColor, font);
            manageRacesButton.addActionListener(e -> {
                RaceManager rm = new RaceManager(this);
                rm.setVisible(true);
                loadData();
                initDriverTable();
                initEchipaTable();
                initCurseTable();
            });
            cursePanel.add(manageRacesButton, BorderLayout.SOUTH);

            JButton editDriverButton = createStyledButton("Editează Driver", accentColor, textColor, font);
            editDriverButton.addActionListener(e -> {
                int selectedRow = driverTable.getSelectedRow();
                if (selectedRow != -1) {
                    Driver selectedDriver = drivers.get(selectedRow);
                    new DriverEditor(this, selectedDriver, echipe).setVisible(true);
                    loadData();
                    initDriverTable();
                    initEchipaTable();
                }
            });
            driverPanel.add(editDriverButton, BorderLayout.SOUTH);

            JButton editTeamButton = createStyledButton("Editează Echipa", accentColor, textColor, font);
            editTeamButton.addActionListener(e -> {
                int selectedRow = echipaTable.getSelectedRow();
                if (selectedRow != -1) {
                    Echipa selectedEchipa = echipe.get(selectedRow);
                    new TeamEditor(this, selectedEchipa).setVisible(true);
                    loadData();
                    initEchipaTable();
                }
            });
            echipaPanel.add(editTeamButton, BorderLayout.SOUTH);
        }

        // Adăugarea unui buton pentru delogare
        JButton logoutButton = createStyledButton("Delogare", accentColor, textColor, font);
        logoutButton.addActionListener(e -> handleLogout());
        JPanel logoutPanel = createStyledPanel(backgroundColor);
        logoutPanel.add(logoutButton, BorderLayout.CENTER);

        // Adăugarea taburilor
        tabbedPane.addTab("Driveri", driverPanel);
        tabbedPane.addTab("Echipe", echipaPanel);
        tabbedPane.addTab("Curse", cursePanel);
        tabbedPane.addTab("Delogare", logoutPanel); // Tab special pentru delogare

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Metodă care gestionează delogarea utilizatorului.
     * Închide fereastra curentă și deschide din nou fereastra de Login.
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
     * Metodă pentru încărcarea datelor din baza de date (driveri, echipe, curse).
     */
    private void loadData() {
        drivers = Driver.loadDriversFromDatabase();
        echipe = Echipa.loadTeamsFromDatabase();
        curse = GP.loadRacesFromDatabase();
    }

    /**
     * Inițializarea tabelului de piloți.
     * Datele sunt sortate după ID pentru un afișaj mai clar.
     */
    private void initDriverTable() {
        String[] columnNames = {"ID", "Nume", "Echipa", "Campionate", "Puncte", "Podiumuri", "Câștiguri"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        drivers.sort(Comparator.comparingInt(Driver::getId));
        for (Driver driver : drivers) {
            Object[] row = {
                    driver.getId(),
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
     * Inițializarea tabelului de echipe.
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
     * Inițializarea tabelului de curse (GP).
     * Datele sunt sortate cronologic (după data cursei).
     */
    private void initCurseTable() {
        String[] columnNames = {"Circuit", "Data", "Distanta", "Momentul din zi", "Sprint"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        curse.sort(Comparator.comparing(GP::getDataGP));
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
     * Aplică un stil unui tabel: culori de fundal, text, fonturi,
     * înălțimea rândurilor și culori pentru grid și antet.
     *
     * @param table       Tabela care va fi stilizată.
     * @param accentColor Culoarea de accent (se folosește pentru header și grid).
     * @param textColor   Culoarea textului.
     * @param cellFont    Fontul utilizat în celule.
     * @param headerFont  Fontul pentru antet.
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

    /**
     * Creează un JPanel cu un layout BorderLayout și fundal personalizat.
     *
     * @param bgColor Culoarea de fundal a panoului.
     * @return Un JPanel configurat.
     */
    private JPanel createStyledPanel(Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    /**
     * Creează un buton cu stil personalizat, folosind culori, font și cursor specific.
     *
     * @param text   Textul afișat pe buton.
     * @param bgColor Culoarea de fundal a butonului.
     * @param fgColor Culoarea textului de pe buton.
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
}


