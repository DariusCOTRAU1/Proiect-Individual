package Proiect;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Clasa responsabilă pentru operațiuni legate de conexiunea la baza de date
 * și pentru actualizarea informațiilor în tabelele corespunzătoare.
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:as.bd";

    /**
     * Returnează o conexiune activă la baza de date, utilizând calea
     * specificată în constanta DB_URL.
     *
     * @return Un obiect de tip {@code Connection} către baza de date.
     * @throws SQLException În cazul în care conexiunea la baza de date eșuează.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Actualizează informațiile despre un pilot (driver) în tabela Drivers.
     *
     * @param driverId       ID-ul pilotului care va fi actualizat.
     * @param nume           Noul nume al pilotului.
     * @param teamId         ID-ul echipei din care face parte pilotul.
     * @param nrCampionate   Numărul actualizat de campionate câștigate de pilot.
     * @param nrPuncte       Numărul actualizat de puncte acumulat de pilot.
     * @param nrPodium       Numărul actualizat de podiumuri obținute de pilot.
     * @param nrCastig       Numărul actualizat de victorii obținute de pilot.
     */
    public static void updateDriver(int driverId, String nume, int teamId, int nrCampionate, int nrPuncte, int nrPodium, int nrCastig) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE Drivers SET nume = ?, team_id = ?, nr_campionate = ?, nr_puncte = ?, nr_podium = ?, nr_castig = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nume);
            stmt.setInt(2, teamId);
            stmt.setInt(3, nrCampionate);
            stmt.setInt(4, nrPuncte);
            stmt.setInt(5, nrPodium);
            stmt.setInt(6, nrCastig);
            stmt.setInt(7, driverId);
            stmt.executeUpdate();
            System.out.println("Driver updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualizează informațiile despre o echipă în tabela Echipe din baza de date.
     *
     * @param teamId        ID-ul echipei care va fi actualizată.
     * @param teamName      Noul nume al echipei.
     * @param totalPoints   Noul punctaj total al echipei.
     * @param nrCampionate  Numărul actualizat de campionate câștigate de echipă.
     */
    public static void updateTeam(int teamId, String teamName, int totalPoints, int nrCampionate) {
        String sql = "UPDATE Echipe SET nume = ?, totalPuncte = ?, nrCampionate = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teamName);
            pstmt.setInt(2, totalPoints);
            pstmt.setInt(3, nrCampionate);
            pstmt.setInt(4, teamId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Echipa a fost actualizată cu succes.");
            } else {
                System.out.println("Actualizarea echipei a eșuat.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la actualizarea echipei în baza de date!");
        }
    }
}
