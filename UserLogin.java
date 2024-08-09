package myPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserLogin extends JFrame {
    private JLabel emailLabel, passwordLabel, headingLabel, createAccountLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public UserLogin() {
        setTitle("User Login");
        setSize(800, 600);
        headingLabel = new JLabel("User Login");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(250, 50));
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 50));
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 50));
        createAccountLabel = new JLabel("Don't have an account? Create one.");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createAccountLabelMouseClicked();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0x6DC5D1));
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
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createAccountLabel, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
        add(panel);
        //pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

       
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

   
        boolean isValidCredentials = checkCredentials(email, password);
        if (isValidCredentials) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(email);
            new Business(userInfo); 
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

            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

       
            boolean isValid = resultSet.next();

            conn.close();
            return isValid;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to validate credentials. Error: " + ex.getMessage());
            return false;
        }
    }

    private void createAccountLabelMouseClicked() {
        // Open a new instance of UserReg class
        new User();
        // Close the current login window
        dispose();
    }

    public static void main(String[] args) {
        new UserLogin();
    }
}