package Proiect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clasa VoteManager se ocupă de gestionarea voturilor pentru piloți
 * în cadrul anumitor curse. Permite înregistrarea unui vot, verificarea
 * dacă un utilizator a votat deja și obținerea pilotului cu cele mai
 * multe voturi pentru o cursă.
 */
public class VoteManager {

    /**
     * Înregistrează un vot pentru un anumit pilot dintr-o cursă, efectuat de un
     * utilizator specific. Restricția este ca fiecare utilizator să voteze o singură
     * dată pentru fiecare cursă.
     *
     * @param raceId   ID-ul cursei pentru care se votează.
     * @param driverId ID-ul pilotului pentru care se acordă votul.
     * @param userId   ID-ul utilizatorului care votează.
     * @return {@code true} dacă votul a fost înregistrat cu succes, {@code false} dacă
     *         utilizatorul a votat deja sau dacă a apărut o eroare de SQL.
     */
    public static boolean castVote(int raceId, int driverId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verifică dacă utilizatorul a mai votat pentru această cursă
            String checkQuery = "SELECT 1 FROM Votes WHERE race_id = ? AND user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, raceId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Utilizatorul a votat deja în această cursă.");
                return false;
            }

            // Înregistrează votul
            String insertQuery = "INSERT INTO Votes (race_id, driver_id, user_id, votes_count) VALUES (?, ?, ?, 1)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, raceId);
            insertStmt.setInt(2, driverId);
            insertStmt.setInt(3, userId);
            insertStmt.executeUpdate();
            System.out.println("Votul a fost înregistrat cu succes!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifică dacă un utilizator a votat deja pentru o anumită cursă.
     *
     * @param raceId ID-ul cursei de verificat.
     * @param userId ID-ul utilizatorului de verificat.
     * @return true dacă utilizatorul a votat deja, false în caz contrar
     *         sau dacă apare o eroare de SQL.
     */
    public static boolean hasUserVoted(int raceId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM Votes WHERE race_id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, raceId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returnează numele pilotului cu cele mai multe voturi pentru o cursă dată.
     *
     * @param raceId ID-ul cursei pentru care se dorește pilotul cu cele mai multe voturi.
     * @return O descriere sub formă de String: "NumePilot cu X voturi" dacă există
     *         voturi, altfel "Niciun vot înregistrat." dacă nu există voturi.
     */
    public static String getDriverOfTheDay(int raceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT D.nume, V.votes_count FROM Votes V "
                    + "JOIN Drivers D ON V.driver_id = D.id "
                    + "WHERE V.race_id = ? "
                    + "ORDER BY V.votes_count DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, raceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nume") + " cu " + rs.getInt("votes_count") + " voturi";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Niciun vot înregistrat.";
    }
}


