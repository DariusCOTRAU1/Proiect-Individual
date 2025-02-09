package Proiect;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Aceasta clasa reprezinta fereastra de login pentru aplicatia F1.
 * Contine campurile de utilizator si parola, precum si butoanele pentru
 * autentificare si inregistrare.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * Constructorul clasei LoginFrame.
     * Configureaza setarile de baza pentru fereastra si initializeaza componentele UI.
     */
    public LoginFrame() {
        setTitle("Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(219, 52, 77);
        Font font = new Font("Segoe UI", Font.PLAIN, 18);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("F1 App/Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(accentColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;

        JLabel userLabel = createStyledLabel("Username:", textColor, font);
        panel.add(userLabel, gbc);
        gbc.gridx++;
        usernameField = createStyledTextField(font);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = createStyledLabel("Password:", textColor, font);
        panel.add(passwordLabel, gbc);
        gbc.gridx++;
        passwordField = createStyledPasswordField(font);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(bgColor);

        JButton loginButton = createStyledButton("Login", accentColor, textColor, font);
        loginButton.addActionListener(e -> authenticateUser());
        buttonPanel.add(loginButton);

        JButton signUpButton = createStyledButton("Sign Up", accentColor, textColor, font);
        signUpButton.addActionListener(e -> openSignUpDialog());
        buttonPanel.add(signUpButton);

        panel.add(buttonPanel, gbc);

        add(panel);
    }

    /**
     * Creeaza un JLabel stilizat cu text, culoare si font specificate.
     *
     * @param text   Textul care va fi afisat in JLabel.
     * @param color  Culoarea textului.
     * @param font   Fontul folosit pentru text.
     * @return       Un JLabel stilizat.
     */
    private JLabel createStyledLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Creeaza un JTextField stilizat cu font specificat.
     *
     * @param font   Fontul folosit pentru textul din JTextField.
     * @return       Un JTextField stilizat.
     */
    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    /**
     * Creeaza un JPasswordField stilizat cu font specificat.
     *
     * @param font   Fontul folosit pentru textul din JPasswordField.
     * @return       Un JPasswordField stilizat.
     */
    private JPasswordField createStyledPasswordField(Font font) {
        JPasswordField field = new JPasswordField();
        field.setFont(font);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    /**
     * Creeaza un JButton stilizat cu text, culori si font specificate.
     *
     * @param text     Textul afisat pe buton.
     * @param bgColor  Culoarea de fundal a butonului.
     * @param fgColor  Culoarea textului de pe buton.
     * @param font     Fontul folosit pentru textul butonului.
     * @return         Un JButton stilizat.
     */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Metoda care se ocupa de autentificarea utilizatorului.
     * Preia numele de utilizator si parola din campuri, verifica in baza de date
     * si, in functie de rol (admin sau user), deschide fereastra potrivita.
     */
    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, role, password FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String passwordFromDB = rs.getString("password");
                String role = rs.getString("role");

                if (password.equals(passwordFromDB)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    if (role.equalsIgnoreCase("admin")) {
                        new MainFrame(role).setVisible(true);
                    } else {
                        new UserFrame(role, userId).setVisible(true);
                    }

                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Incorrect password!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "User not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Deschide un dialog pentru inregistrarea unui nou utilizator.
     * Daca datele sunt valide, se insereaza in baza de date ca utilizator cu rol "user".
     */
    private void openSignUpDialog() {
        JTextField newUsernameField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();

        Object[] message = {
                "Enter a new username:", newUsernameField,
                "Enter a new password:", newPasswordField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Sign Up",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String newUsername = newUsernameField.getText();
            String newPassword = new String(newPasswordField.getPassword());

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'user')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, newUsername);
                stmt.setString(2, newPassword);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during sign up!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Metoda principala de intrare in aplicatie.
     * Creeaza si afiseaza fereastra de login.
     *
     * @param args Argumente din linia de comanda (neutilizate).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
