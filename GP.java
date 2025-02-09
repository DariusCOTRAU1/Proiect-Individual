package Proiect;

import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clasa reprezintă o cursa (Grand Prix - GP) cu detalii precum nume,
 * distanță, data, momentul zilei și specificația dacă include sesiune sprint.
 */
public class GP {
    private int id;
    private String nume;
    private int distanta;
    private LocalDate dataGP;
    private String timeOfDay;
    private boolean isSprint;

    /**
     * Constructorul clasei GP.
     *
     * @param id         ID-ul unic al Cursei.
     * @param nume       Numele Cursei.
     * @param distanta   Distanța totală a cursei, în kilometri.
     * @param dataGP     Data la care are loc Cursa.
     * @param timeOfDay  Momentul zilei în care are loc cursa (de ex. "Day" sau "Night").
     * @param isSprint   Indică dacă cursa include sau nu sesiune de tip sprint.
     */
    public GP(int id, String nume, int distanta, LocalDate dataGP, String timeOfDay, boolean isSprint) {
        this.id = id;
        this.nume = nume;
        this.distanta = distanta;
        this.dataGP = dataGP;
        this.timeOfDay = timeOfDay;
        this.isSprint = isSprint;
    }

    /**
     * Metodă statică ce încarcă toate cursele (Grand Prix) din baza de date.
     *
     * @return O listă (ArrayList) cu obiecte de tip GP, reprezentând cursele încărcate.
     */
    public static ArrayList<GP> loadRacesFromDatabase() {
        ArrayList<GP> races = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:as.bd")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GP");
            while (rs.next()) {
                races.add(new GP(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getInt("distanta"),
                        LocalDate.parse(rs.getString("dataGP")),
                        rs.getString("timeOfDay"),
                        rs.getBoolean("isSprint")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return races;
    }

    /**
     * Returnează ID-ul Cursei.
     *
     * @return ID-ul GP.
     */
    public int getId() {
        return id;
    }

    /**
     * Setează numele Cursei.
     *
     * @param nume Numele nou al GP-ului.
     */
    public void setNume(String nume) {
        this.nume = nume;
    }

    /**
     * Returnează numele Cursei.
     *
     * @return Numele GP-ului.
     */
    public String getNume() {
        return nume;
    }

    /**
     * Returnează distanța totală a Cursei.
     *
     * @return Distanța GP-ului (în kilometri).
     */
    public int getDistanta() {
        return distanta;
    }

    /**
     * Returnează data la care are loc Cursa.
     *
     * @return Data GP-ului (de tip LocalDate).
     */
    public LocalDate getDataGP() {
        return dataGP;
    }

    /**
     * Returnează momentul zilei în care se desfășoară Cursa.
     *
     * @return Momentul zilei (e.g. "Day" sau "Night").
     */
    public String getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * Indică dacă Cursa include o sesiune de tip sprint.
     *
     * @return {@code true} dacă există o sesiune sprint, altfel {@code false}.
     */
    public boolean isSprint() {
        return isSprint;
    }
}
