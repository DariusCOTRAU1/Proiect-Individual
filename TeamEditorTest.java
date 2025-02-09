package Proiect;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class TeamEditorTest {

    private int createdTeamId;
    private Echipa testEchipa;

    /**
     * Înaintea fiecărui test, creăm o echipă de test în DB.
     */
    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("INSERT INTO Echipe (nume, nrCampionate, totalPuncte) VALUES ('TestTeamEditor', 1, 50)");

            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() as lastId");
            if (rs.next()) {
                createdTeamId = rs.getInt("lastId");
            }

            testEchipa = new Echipa(createdTeamId, "TestTeamEditor", 1, 50);
        }
    }

    /**
     * După fiecare test, ștergem echipa de test din DB.
     */
    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Echipe WHERE id = ?")) {
            pstmt.setInt(1, createdTeamId);
            pstmt.executeUpdate();
        }
    }

    @Test
    @DisplayName("TeamEditor salvează noile valori în baza de date")
    void testTeamEditorSaveChanges() throws SQLException {
        TeamEditor editor = new TeamEditor(null, testEchipa);

        JTextField nameField = (JTextField) TestUtils.getFieldValue(editor, "nameField");
        JTextField pointsField = (JTextField) TestUtils.getFieldValue(editor, "pointsField");
        JTextField championshipsField = (JTextField) TestUtils.getFieldValue(editor, "championshipsField");

        nameField.setText("EchipaNoua");
        pointsField.setText("999");
        championshipsField.setText("5");

        editor.saveChanges();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Echipe WHERE id = ?")) {
            pstmt.setInt(1, createdTeamId);
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next(), "Trebuie să existe rândul cu echipa editată.");
            assertEquals("EchipaNoua", rs.getString("nume"));
            assertEquals(999, rs.getInt("totalPuncte"));
            assertEquals(5, rs.getInt("nrCampionate"));
        }
    }
}
