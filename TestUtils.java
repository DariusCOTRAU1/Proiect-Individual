package Proiect;

import java.lang.reflect.Field;

/**
 * Clasa TestUtils oferă metode utilitare pentru testare,
 * în special pentru accesarea prin Reflection a câmpurilor private din alte clase.
 */
public class TestUtils {

    /**
     * Returnează valoarea câmpului (field) specificat din instanța de obiect furnizată,
     * utilizând Reflection pentru a ocoli restricțiile de acces.
     *
     * @param instance  Obiectul din care se dorește extragerea valorii câmpului.
     * @param fieldName Numele câmpului de accesat din clasa obiectului.
     * @return Valoarea câmpului extras.
     * @throws RuntimeException Dacă apar erori la procesarea prin Reflection.
     */
    public static Object getFieldValue(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la reflection: " + e.getMessage(), e);
        }
    }
}
