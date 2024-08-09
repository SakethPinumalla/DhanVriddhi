package myPackage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminRegistration extends JFrame {
    private JLabel nameLabel, emailLabel, dobLabel, roleLabel, passwordLabel, headingLabel;
    private JTextField nameField, emailField, dobField, passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;

    public AdminRegistration() {
        setTitle("Admin Registration");
        headingLabel = new JLabel("Admin Registration");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel = new JLabel("Name:");
        emailLabel = new JLabel("Email:");
        dobLabel = new JLabel("Date of Birth (dd-mm-yyyy):");
        roleLabel = new JLabel("Role:");
        passwordLabel = new JLabel("Password:");
        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(250, 50));
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(250, 50));
        dobField = new JTextField(20);
        dobField.setPreferredSize(new Dimension(250, 50));
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 50));
        roleComboBox = new JComboBox<>(new String[]{"Manager", "Supervisor", "Coordinator"});
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String dob = dobField.getText();
                String role = (String) roleComboBox.getSelectedItem();
                String password = new String(((JPasswordField) passwordField).getPassword());
                
                registerAdmin(name, email, dob, role, password);
            }
        });

        // Set up panel with grid layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xCDE8E5)); // Set background color

        // Set up GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add components to panel
        panel.add(headingLabel, gbc);
        panel.add(nameLabel, gbc);
        panel.add(nameField, gbc);
        panel.add(emailLabel, gbc);
        panel.add(emailField, gbc);
        panel.add(dobLabel, gbc);
        panel.add(dobField, gbc);
        panel.add(passwordLabel, gbc);
        panel.add(passwordField, gbc);
        panel.add(roleLabel, gbc);
        panel.add(roleComboBox, gbc);
        panel.add(registerButton, gbc);

        // Add panel to frame
        add(panel);

        // Set frame properties
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void registerAdmin(String name, String email, String dobText, String role, String password) {
        try {
            Date dob = new SimpleDateFormat("dd-MM-yyyy").parse(dobText);
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passwordDb = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, passwordDb);

            String query = "INSERT INTO admin (name, email, dob, role, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setDate(3, new java.sql.Date(dob.getTime()));
            statement.setString(4, role);
            statement.setString(5, password);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Admin registered successfully!");
            }
            conn.close();
        } catch (SQLException | ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to register admin. Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminRegistration();
            }
        });
    }
}
