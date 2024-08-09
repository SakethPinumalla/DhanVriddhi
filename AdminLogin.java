package myPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin extends JFrame {
    private JLabel emailLabel, passwordLabel, headingLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(800, 600);
        headingLabel = new JLabel("Admin Login");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(250,50));
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250,50));
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100,50));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0x7AB2B2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        panel.add(headingLabel, gbc);
        panel.add(emailLabel, gbc);
        panel.add(emailField, gbc);
        panel.add(passwordLabel, gbc);
        panel.add(passwordField, gbc);
        panel.add(loginButton, gbc);

        // Add panel to frame
        add(panel);

        // Set frame properties
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAdmin();
            }
        });
    }

    private void loginAdmin() {
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        // Perform database lookup
        boolean isValidCredentials = checkCredentials(email, password);
        if (isValidCredentials) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new AdminInterface(email);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.");
        }
    }

    private boolean checkCredentials(String email, String password) {
        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String dbPassword = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, dbPassword);

            String query = "SELECT * FROM admin WHERE email = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            // If resultSet has any rows, credentials are valid
            boolean isValid = resultSet.next();

            conn.close();
            return isValid;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to validate credentials. Error: " + ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}

