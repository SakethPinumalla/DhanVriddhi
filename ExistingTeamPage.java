package myPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ExistingTeamPage extends JFrame {
    private int userId;
    private JComboBox<String> businessComboBox;
    private JTextField investmentAmountTextField;
    private JButton submitButton;
    private JPanel teamPanel;

    public ExistingTeamPage(int userId) {
        this.userId = userId;

        setTitle("Existing Team Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(0xFFFFFF));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading Label
        JLabel headingLabel = new JLabel("Existing Team Page", JLabel.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(headingLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0xFFFFFF));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Investment Details", 0, 0, new Font("Arial", Font.BOLD, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // JComboBox for business names
        String[] businessNames = {"Dairy", "Poultry", "Food Court", "Franchise"};
        businessComboBox = new JComboBox<>(businessNames);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Business: "), gbc);
        gbc.gridx = 1;
        formPanel.add(businessComboBox, gbc);

        // Investment Amount TextField
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Investment Amount:"), gbc);
        investmentAmountTextField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(investmentAmountTextField, gbc);

        // Submit Button
        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0x4CAF50));
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(submitButton, gbc);
        mainPanel.add(formPanel, BorderLayout.WEST);

        // Team Panel
        teamPanel = new JPanel();
        teamPanel.setLayout(new BoxLayout(teamPanel, BoxLayout.Y_AXIS));
        teamPanel.setBackground(new Color(0xFFFFFF));
        JScrollPane scrollPane = new JScrollPane(teamPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Teams", 0, 0, new Font("Arial", Font.BOLD, 16)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add listener to submitButton
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBusiness = (String) businessComboBox.getSelectedItem();
                String amountText = investmentAmountTextField.getText();
                if (isValidAmount(amountText)) {
                    fetchAndDisplayTeams(selectedBusiness);
                } else {
                    JOptionPane.showMessageDialog(ExistingTeamPage.this, "Invalid investment amount. Please enter an amount greater than 100,000.");
                }
            }
        });

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method to fetch and display teams based on selected business
    private void fetchAndDisplayTeams(String selectedBusiness) {
        // Clear previous team panels
        teamPanel.removeAll();
        teamPanel.revalidate();
        teamPanel.repaint();

        try {
            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String dbPassword = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, dbPassword);

            // Prepare the SQL statement
            String sql = "SELECT * FROM team WHERE business_id IN (SELECT business_id FROM business WHERE name = ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, selectedBusiness);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the results and display team panels
            while (resultSet.next()) {
                String teamName = resultSet.getString("team_name");
                String description = resultSet.getString("description");
                int vacancy = resultSet.getInt("vacancy");
                double totalProfits = resultSet.getDouble("total_profits");
                JPanel teamEntryPanel = createTeamEntryPanel(teamName, description, vacancy, totalProfits);
                teamPanel.add(teamEntryPanel);
            }

            // Close the connection
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ExistingTeamPage.this, "Failed to fetch team details. Error: " + ex.getMessage());
        }
    }

    // Method to create a panel for each team entry
    private JPanel createTeamEntryPanel(String teamName, String description, int vacancy, double totalProfits) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(0xF0F0F0));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x4CAF50), 2));

        JLabel nameLabel = new JLabel("Team Name: " + teamName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel descLabel = new JLabel("<html><body style='width: 200px'>" + "Description: " + description + "</body></html>");
        JLabel vacancyLabel = new JLabel("Vacancy: " + vacancy);
        JLabel profitsLabel = new JLabel("Total Profits: " + totalProfits);

        JButton joinButton = new JButton("Join");
        joinButton.setBackground(new Color(0x4CAF50));
        joinButton.setForeground(Color.WHITE);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vacancy > 0) {
                    try {
                        int teamid = getTeamIdFromTeamName(teamName);
                        double amount = Double.parseDouble(investmentAmountTextField.getText());
                        int businessId = getBusinessIdFromTeamId(teamid);
                        insertIntoTeamUserTable(userId, teamName);
                        insertIntoInvestmentTable(userId, businessId, amount);
                        updateVacancy(teamName, vacancy - 1);
                        fetchAndDisplayTeams((String) businessComboBox.getSelectedItem());
                        new TeamInterfacePage(teamid,userId);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ExistingTeamPage.this, "Failed to join team. Error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(ExistingTeamPage.this, "Vacancy for this team is already filled.");
                }
            }
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(0xF0F0F0));
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(vacancyLabel);
        infoPanel.add(profitsLabel);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(joinButton, BorderLayout.EAST);

        return panel;
    }

    // Method to validate investment amount
    private boolean isValidAmount(String amountText) {
        try {
            int amount = Integer.parseInt(amountText);
            return amount >= 100000;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void insertIntoInvestmentTable(int userId, int businessId, double amount) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String insertQuery = "INSERT INTO investment (user_id, business_id, amount) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, businessId);
                insertStatement.setDouble(3, amount);
                insertStatement.executeUpdate();
            }
        }
    }

    // Method to insert values into the team_user table
    private void insertIntoTeamUserTable(int userId, String teamName) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            int teamId = getTeamIdFromTeamName(teamName);
            String insertQuery = "INSERT INTO team_user (team_id, user_id) VALUES (?, ?)";
            try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, teamId);
                insertStatement.setInt(2, userId);
                insertStatement.executeUpdate();
            }
        }
    }

    // Method to update vacancy count in the team table
    private void updateVacancy(String teamName, int vacancy) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String updateQuery = "UPDATE team SET vacancy = ? WHERE team_name = ?";
            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, vacancy);
                updateStatement.setString(2, teamName);
                updateStatement.executeUpdate();
            }
        }
    }

    // Method to retrieve team_id based on the selected team name
    private int getTeamIdFromTeamName(String teamName) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String query = "SELECT team_id FROM team WHERE team_name = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, teamName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("team_id");
                    }
                }
            }
        }
        return -1;
    }

    private int getBusinessIdFromTeamId(int teamId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakethdb", "root", "saketh21@vce")) {
            String query = "SELECT business_id FROM team WHERE team_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, teamId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("business_id");
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int userId = 4; // Replace with actual user ID
                new ExistingTeamPage(userId);
            }
        });
    }
}
