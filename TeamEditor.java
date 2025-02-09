package Proiect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clasa TeamEditor reprezintă un dialog (fereastră modală) pentru editarea informațiilor
 * despre o echipă din Formula 1, precum numele echipei, numărul total de puncte și
 * numărul de campionate câștigate. După editare, datele sunt actualizate în baza de date.
 */
public class TeamEditor extends JDialog {
    private Echipa echipa;
    private JTextField nameField;
    private JTextField pointsField;
    private JTextField championshipsField;

    /**
     * Constructorul pentru fereastra de editare a unei echipe.
     *
     * @param parent Fereastra părinte (JFrame) a acestui dialog.
     * @param echipa Obiectul Echipa ale cărei detalii vor fi editate.
     */
    public TeamEditor(JFrame parent, Echipa echipa) {
        super(parent, "Editează Echipa", true);
        this.echipa = echipa;

        Color backgroundColor = Color.BLACK;
        Color panelBackground = new Color(40, 40, 40);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(231, 76, 60);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout());

        // Panou pentru câmpurile de editare
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(panelBackground);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Adăugare etichete și câmpuri de text
        formPanel.add(createStyledLabel("Nume Echipa:", labelFont, textColor));
        nameField = createStyledTextField(echipa.getNume(), fieldFont, textColor);
        formPanel.add(nameField);

        formPanel.add(createStyledLabel("Puncte totale:", labelFont, textColor));
        pointsField = createStyledTextField(String.valueOf(echipa.getTotalPuncte()), fieldFont, textColor);
        formPanel.add(pointsField);

        formPanel.add(createStyledLabel("Număr Campionate:", labelFont, textColor));
        championshipsField = createStyledTextField(String.valueOf(echipa.getNrCampionate()), fieldFont, textColor);
        formPanel.add(championshipsField);

        add(formPanel, BorderLayout.CENTER);

        // Panou pentru butonul de salvare
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(panelBackground);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JButton saveButton = new JButton("Salvează");
        saveButton.setFont(buttonFont);
        saveButton.setBackground(accentColor);
        saveButton.setForeground(textColor);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveChanges());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Creează un JLabel cu stil personalizat.
     *
     * @param text      Textul care va fi afișat.
     * @param font      Fontul folosit pentru text.
     * @param textColor Culoarea textului.
     * @return Un obiect JLabel stilizat.
     */
    private JLabel createStyledLabel(String text, Font font, Color textColor) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(textColor);
        return label;
    }

    /**
     * Creează un JTextField cu stil personalizat și text inițial.
     *
     * @param text      Textul inițial afișat în câmp.
     * @param font      Fontul folosit pentru text.
     * @param textColor Culoarea textului și a cursorului de inserție.
     * @return Un obiect JTextField stilizat.
     */
    private JTextField createStyledTextField(String text, Font font, Color textColor) {
        JTextField textField = new JTextField(text);
        textField.setFont(font);
        textField.setForeground(textColor);
        textField.setBackground(new Color(64, 64, 64));
        textField.setCaretColor(textColor);
        textField.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));
        return textField;
    }

    /**
     * Metodă care salvează modificările făcute asupra obiectului Echipa
     * și actualizează informațiile în baza de date.
     */
    void saveChanges() {
        try {
            // Actualizează valorile în obiectul echipa
            echipa.setNume(nameField.getText());
            echipa.setTotalPoints(Integer.parseInt(pointsField.getText()));
            echipa.setNrCampionate(Integer.parseInt(championshipsField.getText()));

            // Actualizează în baza de date
            DatabaseConnection.updateTeam(
                    echipa.getId(),
                    echipa.getNume(),
                    echipa.getTotalPuncte(),
                    echipa.getNrCampionate()
            );

            JOptionPane.showMessageDialog(this, "Echipa a fost actualizată cu succes!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introduceți valori numerice valide pentru câmpurile numerice.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

