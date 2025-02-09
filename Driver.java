package Proiect;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clasa reprezintă un pilot (driver) din Formula 1, cu informații despre ID-ul
 * pilotului, nume, ID-ul echipei din care face parte, numărul de puncte, numărul
 * de podiumuri, numărul de victorii și numărul de campionate câștigate.
 */
public class Driver {
    private int id;
    private String nume;
    private int teamId;         // referință la Echipe (id)
    private int nrPuncte;
    private int nrPodium;
    private int nrCastig;
    private int nrCampionate;

    /**
     * Constructorul clasei Driver.
     *
     * @param id            ID-ul pilotului.
     * @param nume          Numele pilotului.
     * @param teamId        ID-ul echipei din care face parte pilotul.
     * @param nrPuncte      Numărul de puncte acumulate de pilot.
     * @param nrPodium      Numărul de podiumuri obținute de pilot.
     * @param nrCastig      Numărul de victorii obținute de pilot.
     * @param nrCampionate  Numărul de campionate câștigate de pilot.
     */
    public Driver(int id, String nume, int teamId, int nrPuncte,
                  int nrPodium, int nrCastig, int nrCampionate) {
        this.id = id;
        this.nume = nume;
        this.teamId = teamId;
        this.nrPuncte = nrPuncte;
        this.nrPodium = nrPodium;
        this.nrCastig = nrCastig;
        this.nrCampionate = nrCampionate;
    }

    /**
     * Returnează ID-ul pilotului.
     *
     * @return ID-ul pilotului.
     */
    public int getId() {
        return id;
    }

    /**
     * Setează ID-ul pilotului.
     *
     * @param id Noua valoare a ID-ului pilotului.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returnează numele pilotului.
     *
     * @return Numele pilotului.
     */
    public String getNume() {
        return nume;
    }

    /**
     * Setează numele pilotului.
     *
     * @param nume Numele nou al pilotului.
     */
    public void setNume(String nume) {
        this.nume = nume;
    }

    /**
     * Returnează ID-ul echipei din care face parte pilotul.
     *
     * @return ID-ul echipei.
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Setează ID-ul echipei din care face parte pilotul.
     *
     * @param teamId Noua valoare a ID-ului echipei.
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Returnează numărul de puncte acumulate de pilot.
     *
     * @return Numărul de puncte.
     */
    public int getNrPuncte() {
        return nrPuncte;
    }

    /**
     * Setează numărul de puncte acumulate de pilot.
     *
     * @param nrPuncte Noua valoare a punctelor.
     */
    public void setNrPuncte(int nrPuncte) {
        this.nrPuncte = nrPuncte;
    }

    /**
     * Returnează numărul de podiumuri obținute de pilot.
     *
     * @return Numărul de podiumuri.
     */
    public int getNrPodium() {
        return nrPodium;
    }

    /**
     * Setează numărul de podiumuri obținute de pilot.
     *
     * @param nrPodium Noua valoare a podiumurilor.
     */
    public void setNrPodium(int nrPodium) {
        this.nrPodium = nrPodium;
    }

    /**
     * Returnează numărul de victorii obținute de pilot.
     *
     * @return Numărul de victorii.
     */
    public int getNrCastig() {
        return nrCastig;
    }

    /**
     * Setează numărul de victorii obținute de pilot.
     *
     * @param nrCastig Noua valoare a câștigurilor.
     */
    public void setNrCastig(int nrCastig) {
        this.nrCastig = nrCastig;
    }

    /**
     * Returnează numărul de campionate câștigate de pilot.
     *
     * @return Numărul de campionate.
     */
    public int getNrCampionate() {
        return nrCampionate;
    }

    /**
     * Setează numărul de campionate câștigate de pilot.
     *
     * @param nrCampionate Noua valoare a campionatelor.
     */
    public void setNrCampionate(int nrCampionate) {
        this.nrCampionate = nrCampionate;
    }

    /**
     * Încarcă toți driverii din baza de date și îi returnează sub formă
     * de listă . Metoda folosește un JOIN pentru a lega datele
     * din tabelele "Drivers" și "Echipe".
     *
     * @return O listă cu obiecte de tip Driver.
     */
    public static ArrayList<Driver> loadDriversFromDatabase() {
        ArrayList<Driver> drivers = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:as.bd")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Drivers D JOIN Echipe E ON D.team_id = E.id");
            while (rs.next()) {
                Driver d = new Driver(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getInt("team_id"),
                        rs.getInt("nr_puncte"),
                        rs.getInt("nr_podium"),
                        rs.getInt("nr_castig"),
                        rs.getInt("nr_campionate")
                );
                drivers.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    /**
     * Metodă care returnează direct numele echipei pentru acest pilot,
     * folosindu-se de team_id și căutând numele echipei în baza de date.
     *
     * @return Numele echipei (String) dacă este găsit, altfel "N/A".
     */
    public String getTeamNameFromDB() {
        String teamName = "N/A";
        String sql = "SELECT nume FROM Echipe WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:as.bd");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.teamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    teamName = rs.getString("nume");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teamName;
    }
}
