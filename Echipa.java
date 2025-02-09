package Proiect;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clasa reprezintă o echipă de Formula 1, cu informații despre nume, numărul
 * de campionate câștigate și numărul total de puncte acumulate.
 */
public class Echipa {
    private int id;
    private String nume;
    private int nrCampionate;
    private int totalPuncte;

    /**
     * Constructorul clasei Echipa.
     *
     * @param id             ID-ul unic al echipei.
     * @param nume           Numele echipei.
     * @param nrCampionate   Numărul de campionate câștigate de echipă.
     * @param totalPuncte    Numărul total de puncte acumulate de echipă.
     */
    public Echipa(int id, String nume, int nrCampionate, int totalPuncte) {
        this.id = id;
        this.nume = nume;
        this.nrCampionate = nrCampionate;
        this.totalPuncte = totalPuncte;
    }

    /**
     * Returnează ID-ul echipei.
     *
     * @return ID-ul echipei.
     */
    public int getId() {
        return id;
    }

    /**
     * Returnează numele echipei.
     *
     * @return Numele echipei.
     */
    public String getNume() {
        return nume;
    }

    /**
     * Setează numele echipei.
     *
     * @param nume Noul nume al echipei.
     */
    public void setNume(String nume) {
        this.nume = nume;
    }

    /**
     * Returnează numărul de campionate câștigate de echipă.
     *
     * @return Numărul de campionate câștigate.
     */
    public int getNrCampionate() {
        return nrCampionate;
    }

    /**
     * Setează numărul de campionate câștigate de echipă.
     *
     * @param nrCampionate Noua valoare a campionatelor câștigate.
     */
    public void setNrCampionate(int nrCampionate) {
        this.nrCampionate = nrCampionate;
    }

    /**
     * Returnează numărul total de puncte acumulate de echipă.
     *
     * @return Numărul total de puncte.
     */
    public int getTotalPuncte() {
        return totalPuncte;
    }

    /**
     * Setează numărul total de puncte acumulate de echipă.
     *
     * @param totalPuncte Noua valoare a punctelor.
     */
    public void setTotalPoints(int totalPuncte) {
        this.totalPuncte = totalPuncte;
    }

    /**
     * Încarcă toate echipele din baza de date.
     *
     * @return O listă (ArrayList) care conține toate echipele încărcate.
     */
    public static ArrayList<Echipa> loadTeamsFromDatabase() {
        ArrayList<Echipa> echipe = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:as.bd")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Echipe");
            while (rs.next()) {
                Echipa e = new Echipa(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getInt("nrCampionate"),
                        rs.getInt("totalPuncte")
                );
                echipe.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return echipe;
    }

    /**
     * Întoarce toți piloții (Drivers) asociați acestei echipe.
     *
     * @return O listă (ArrayList) cu obiecte de tip Driver pentru echipa curentă.
     */
    public ArrayList<Proiect.Driver> getDriversForTeam() {
        ArrayList<Proiect.Driver> drivers = new ArrayList<>();

        String sql = "SELECT * FROM Drivers WHERE team_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:as.bd");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Proiect.Driver d = new Driver(
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drivers;
    }
}
