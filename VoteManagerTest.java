package Proiect;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class VoteManagerTest {
    private static final int TEST_RACE_ID = 9999;
    private static final int TEST_USER_ID = 12345;
    private static final int TEST_DRIVER_ID = 1;

    /**
     * Șterge intrările anterioare din tabela Votes pentru cursa de test,
     * astfel încât fiecare test începe „curat”.
     */
    @BeforeEach
    void cleanUpVotes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM Votes WHERE race_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, TEST_RACE_ID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Eroare la curățarea datelor de test din tabela Votes.");
        }
    }

    @Test
    @DisplayName("hasUserVoted returnează false dacă user-ul n-a votat încă")
    void testHasUserVotedBeforeVoting() {
        boolean hasVoted = VoteManager.hasUserVoted(TEST_RACE_ID, TEST_USER_ID);
        assertFalse(hasVoted, "User-ul nu ar fi trebuit să aibă un vot în tabela Votes.");
    }

    @Test
    @DisplayName("castVote permite un prim vot, dar respinge al doilea vot la aceeași cursă/user")
    void testCastVote() {

        boolean firstVote = VoteManager.castVote(TEST_RACE_ID, TEST_DRIVER_ID, TEST_USER_ID);
        assertTrue(firstVote, "Primul vot ar trebui să fie valid (true).");


        boolean secondVote = VoteManager.castVote(TEST_RACE_ID, TEST_DRIVER_ID, TEST_USER_ID);
        assertFalse(secondVote, "Al doilea vot la aceeași cursă ar trebui să fie respins (false).");
    }

    @Test
    @DisplayName("hasUserVoted returnează true dacă user-ul a votat deja")
    void testHasUserVotedAfterVoting() {

        VoteManager.castVote(TEST_RACE_ID, TEST_DRIVER_ID, TEST_USER_ID);


        boolean hasVoted = VoteManager.hasUserVoted(TEST_RACE_ID, TEST_USER_ID);
        assertTrue(hasVoted, "User-ul a votat, deci ar trebui să fie true.");
    }

    @Test
    @DisplayName("getDriverOfTheDay - returnează pilotul cu cele mai multe voturi")
    void testGetDriverOfTheDay() {

        VoteManager.castVote(TEST_RACE_ID, 1, 11111);
        VoteManager.castVote(TEST_RACE_ID, 2, 22222);
        VoteManager.castVote(TEST_RACE_ID, 1, 33333);
        VoteManager.castVote(TEST_RACE_ID, 2, 44444);
        VoteManager.castVote(TEST_RACE_ID, 1, 55555);


        String driverOfTheDay = VoteManager.getDriverOfTheDay(TEST_RACE_ID);


        assertTrue(driverOfTheDay.contains("3 voturi"), "Driver of the Day ar trebui să aibă 3 voturi");

        assertTrue(driverOfTheDay.contains("Test Driver") || driverOfTheDay.contains("Max Verstappen"),
                "Driver of the Day ar trebui să fie cel cu ID=1 (3 voturi). Rezultat actual: " + driverOfTheDay);
    }
}
