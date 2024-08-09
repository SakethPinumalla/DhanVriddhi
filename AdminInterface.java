package myPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class AdminInterface extends JFrame {
    private JLabel headingLabel;
    private JPanel businessPanel;
    private String adminEmail;

    // Twilio Credentials
    private static final String ACCOUNT_SID = "ACf01a8f4391b8ed8487f968b7ef6f578c";
    private static final String AUTH_TOKEN = "361904fcae7bcfdde54be2d7d8a1cb61";
    private static final String TWILIO_PHONE_NUMBER = "+14793705431";

    public AdminInterface(String adminEmail) {
        this.adminEmail = adminEmail;
        setTitle("Admin Interface");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        headingLabel = new JLabel("DhanVriddhi", JLabel.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 48));
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(headingLabel, BorderLayout.NORTH);

        businessPanel = new JPanel(new GridBagLayout());
        businessPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(businessPanel);
        scrollPane.setPreferredSize(new Dimension(780, 520));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Businesses", 0, 0, new Font("Arial", Font.BOLD, 16)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        fetchAndDisplayBusinesses();
    }

    private void fetchAndDisplayBusinesses() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            int adminId = getAdminIdByEmail(conn, adminEmail);
            ResultSet resultSet = getBusinessesByAdminId(conn, adminId);
            displayBusinesses(resultSet);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to fetch and display businesses. Error: " + ex.getMessage());
        }
    }

    private int getAdminIdByEmail(Connection conn, String email) throws SQLException {
        String query = "SELECT admin_id FROM admin WHERE email = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("admin_id");
                } else {
                    throw new SQLException("Admin not found for email: " + email);
                }
            }
        }
    }

    private ResultSet getBusinessesByAdminId(Connection conn, int adminId) throws SQLException {
        String query = "SELECT * FROM business WHERE admin_id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, adminId);
        return statement.executeQuery();
    }

    private void displayBusinesses(ResultSet resultSet) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        try {
            while (resultSet.next()) {
                int businessId = resultSet.getInt("business_id");

                JPanel businessPanelItem = new JPanel(new BorderLayout(10, 10));
                businessPanelItem.setBackground(new Color(0xF0F0F0));
                businessPanelItem.setBorder(BorderFactory.createLineBorder(new Color(0x4CAF50), 2));

                JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                infoPanel.setBackground(new Color(0xF0F0F0));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel nameLabel = new JLabel("Business: " + resultSet.getString("name"));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                JLabel descriptionLabel = new JLabel("Location: " + resultSet.getString("location"));
                JLabel teamLabel = new JLabel("Team Name: " + getTeamNameForBusinessId(businessId));

                infoPanel.add(nameLabel);
                infoPanel.add(descriptionLabel);
                infoPanel.add(teamLabel);

                JButton addDataButton = new JButton("Add Data");
                addDataButton.setBackground(new Color(0x4CAF50));
                addDataButton.setForeground(Color.WHITE);
                addDataButton.addActionListener(e -> showDataEntryDialog(businessId));

                businessPanelItem.add(infoPanel, BorderLayout.CENTER);
                businessPanelItem.add(addDataButton, BorderLayout.EAST);

                businessPanel.add(businessPanelItem, gbc);
                gbc.gridy++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to display businesses. Error: " + ex.getMessage());
        }
    }

    private String getTeamNameForBusinessId(int businessId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String query = "SELECT team_name FROM team WHERE business_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, businessId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("team_name");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to fetch team name. Error: " + ex.getMessage());
        }
        return null;
    }

    private void showDataEntryDialog(int businessId) {
        JDialog dialog = new JDialog(this, "Enter Data for Business ID: " + businessId, true);
        dialog.setSize(400, 300);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel transactionPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField dateField = new JTextField();
        JTextField transactionIdField = new JTextField();
        JComboBox<String> transactionTypeBox = new JComboBox<>(new String[]{"Credit", "Debit"});
        JTextField transactionAmountField = new JTextField();
        
        transactionPanel.add(new JLabel("Date (dd-MM-yyyy):"));
        transactionPanel.add(dateField);
        transactionPanel.add(new JLabel("Transaction ID:"));
        transactionPanel.add(transactionIdField);
        transactionPanel.add(new JLabel("Transaction Type:"));
        transactionPanel.add(transactionTypeBox);
        transactionPanel.add(new JLabel("Transaction Amount:"));
        transactionPanel.add(transactionAmountField);

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setBackground(new Color(0x4CAF50));
        addTransactionButton.setForeground(Color.WHITE);
        addTransactionButton.addActionListener(e -> {
            try {
                String date = dateField.getText();
                String transactionId = transactionIdField.getText();
                String transactionType = (String) transactionTypeBox.getSelectedItem();
                double transactionAmount = Double.parseDouble(transactionAmountField.getText());
                saveTransactionToDatabase(businessId, date, transactionId, transactionType, transactionAmount);
                transactionIdField.setText("");
                transactionAmountField.setText("");
                notifyInvestors(businessId, date, transactionType, transactionAmount);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid input: " + ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit All");
        submitButton.setBackground(new Color(0x4CAF50));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            try {
                String date = dateField.getText();
                calculateAndSaveDailyTotals(businessId, date);
                dialog.dispose();
            } catch (ParseException ex) {
                showErrorDialog("Invalid date format: " + ex.getMessage());
            }
        });

        buttonPanel.add(addTransactionButton);
        buttonPanel.add(submitButton);

        panel.add(transactionPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void saveTransactionToDatabase(int businessId, String date, String transactionId, String transactionType, double transactionAmount) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String query = "INSERT INTO transactions (transaction_id, business_id, date, transaction_type, amount) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, transactionId);
                statement.setInt(2, businessId);
                statement.setString(3, convertDateFormat(date, "dd-MM-yyyy", "yyyy-MM-dd"));
                statement.setString(4, transactionType);
                statement.setDouble(5, transactionAmount);
                statement.executeUpdate();
            }
        } catch (SQLException | ParseException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to save transaction to database. Error: " + ex.getMessage());
        }
    }

    private void calculateAndSaveDailyTotals(int businessId, String date) throws ParseException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String query = "SELECT transaction_type, amount FROM transactions WHERE business_id = ? AND date = ?";
            double totalRevenue = 0.0;
            double totalExpenses = 0.0;

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, businessId);
                statement.setString(2, convertDateFormat(date, "dd-MM-yyyy", "yyyy-MM-dd"));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String transactionType = resultSet.getString("transaction_type");
                        double transactionAmount = resultSet.getDouble("amount");

                        if ("Credit".equalsIgnoreCase(transactionType)) {
                            totalRevenue += transactionAmount;
                        } else if ("Debit".equalsIgnoreCase(transactionType)) {
                            totalExpenses += transactionAmount;
                        }
                    }
                }
            }

            String sqlDate = convertDateFormat(date, "dd-MM-yyyy", "yyyy-MM-dd");

            String insertQuery = "INSERT INTO business_data (business_id, date, revenue, expenses) VALUES (?, ?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE revenue = VALUES(revenue), expenses = VALUES(expenses)";
            try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
                statement.setInt(1, businessId);
                statement.setString(2, sqlDate);
                statement.setDouble(3, totalRevenue);
                statement.setDouble(4, totalExpenses);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data added successfully for Business ID: " + businessId);
                notifyInvestors(businessId, date, "Daily Total", totalRevenue);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to save daily totals to database. Error: " + ex.getMessage());
        }
    }

    private void notifyInvestors(int businessId, String date, String transactionType, double transactionAmount) {
        List<String> investorPhones = getInvestorPhoneNumbers(businessId);
        String message = "DhanVriddhi: Transaction " + transactionType + " for Business ID: " + businessId + " on " + date + " Amount: " + transactionAmount;

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        for (String phone : investorPhones) {
            sendSms(phone, message);
        }
    }

    private List<String> getInvestorPhoneNumbers(int businessId) {
        List<String> phoneNumbers = new ArrayList<>();
        String query = "SELECT u.mobile FROM user u " +
                       "JOIN investment i ON u.user_id = i.user_id " +
                       "WHERE i.business_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce");
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, businessId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    phoneNumbers.add(resultSet.getString("mobile"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to fetch investor phone numbers. Error: " + ex.getMessage());
        }
        return phoneNumbers;
    }

    private void sendSms(String to, String message) {
        Message.creator(
            new PhoneNumber(to),
            new PhoneNumber(TWILIO_PHONE_NUMBER),
            message
        ).create();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String convertDateFormat(String date, String fromFormat, String toFormat) throws ParseException {
        SimpleDateFormat fromDateFormat = new SimpleDateFormat(fromFormat);
        SimpleDateFormat toDateFormat = new SimpleDateFormat(toFormat);
        return toDateFormat.format(fromDateFormat.parse(date));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String adminEmail = "ramana@gmail.com"; // Replace with actual email
            new AdminInterface(adminEmail);
        });
    }
}
