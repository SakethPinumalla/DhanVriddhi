package myPackage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class User extends JFrame {
    private JLabel fnLabel, lnLabel, emailLabel, mobileLabel, dobLabel, passwordLabel, headingLabel, profilePicLabel;
    private JTextField fnField, lnField, emailField, mobileField, dobField;
    private JPasswordField passwordField;
    private JButton registerButton, profilePicButton;
    private File profilePicFile;

    public User() {
        setTitle("User Registration");
        setSize(800, 600);
        headingLabel = new JLabel("User Registration");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        fnLabel = new JLabel("First Name:");
        lnLabel = new JLabel("Last Name:");
        emailLabel = new JLabel("Email:");
        mobileLabel = new JLabel("Mobile:");
        dobLabel = new JLabel("Date of Birth (dd-mm-yyyy):");
        passwordLabel = new JLabel("Password:");
        profilePicLabel = new JLabel("Profile Picture:");

        fnField = new JTextField();
        fnField.setPreferredSize(new Dimension(250, 50));
        lnField = new JTextField();
        lnField.setPreferredSize(new Dimension(250, 50));
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 50));
        mobileField = new JTextField();
        mobileField.setPreferredSize(new Dimension(250, 50));
        dobField = new JTextField();
        dobField.setPreferredSize(new Dimension(250, 50));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 50));

        profilePicButton = new JButton("Choose Profile Picture");
        profilePicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    profilePicFile = fileChooser.getSelectedFile();
                    if (!profilePicFile.getName().toLowerCase().endsWith(".jpg") && !profilePicFile.getName().toLowerCase().endsWith(".jpeg")) {
                        JOptionPane.showMessageDialog(null, "Only JPG files are allowed.");
                        profilePicFile = null;
                    }
                }
            }
        });

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fn = fnField.getText();
                String ln = lnField.getText();
                String email = emailField.getText();
                String mobile = mobileField.getText();
                String dobText = dobField.getText();
                String password = new String(passwordField.getPassword());
                Date dob = null;
                try {
                    dob = new SimpleDateFormat("dd-MM-yyyy").parse(dobText); 
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please use dd-mm-yyyy.");
                    return;
                }

                registerUser(fn, ln, email, mobile, dob, password, profilePicFile);
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0x6DC5D1)); // Set background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(headingLabel, gbc);
        panel.add(fnLabel, gbc);
        panel.add(fnField, gbc);
        panel.add(lnLabel, gbc);
        panel.add(lnField, gbc);
        panel.add(emailLabel, gbc);
        panel.add(emailField, gbc);
        panel.add(mobileLabel, gbc);
        panel.add(mobileField, gbc);
        panel.add(dobLabel, gbc);
        panel.add(dobField, gbc);
        panel.add(passwordLabel, gbc);
        panel.add(passwordField, gbc);
        panel.add(profilePicLabel, gbc);
        panel.add(profilePicButton, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(registerButton, gbc);

        add(panel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void registerUser(String fn, String ln, String email, String mobile, Date dob, String password, File profilePicFile) {
        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, passworddb);

            String query = "INSERT INTO user (fn, ln, email, mobile, dob, earnings, investments, password, profile_picture) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, fn);
            statement.setString(2, ln);
            statement.setString(3, email);
            statement.setString(4, mobile);
            statement.setDate(5, new java.sql.Date(dob.getTime()));
            statement.setBigDecimal(6, BigDecimal.ZERO);
            statement.setBigDecimal(7, BigDecimal.ZERO);
            statement.setString(8, password);

            if (profilePicFile != null) {
                FileInputStream fis = new FileInputStream(profilePicFile);
                statement.setBlob(9, fis);
            } else {
                statement.setNull(9, java.sql.Types.BLOB);
            }

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
            	JOptionPane.showMessageDialog(null, "User registered:\nFirst Name: " + fn + "\nLast Name: " + ln + "\nEmail: " + email + "\nMobile: " + mobile + "\nDate of Birth: " + dob + "\nPassword: " + password);
                new UserLogin();
            }
            conn.close();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to register user. Error: " + ex.getMessage());
        } 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new User();
            }
        });
    }
}
