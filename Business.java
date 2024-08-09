package myPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Business extends JFrame {
    private JLabel headingLabel;
    private JButton newInvestmentButton;
    private JLabel goToMyInvestments;
    private JLabel userDash;
    private JLabel LeaderBoard;
    private JLabel goToMyTeams;
    private UserInfo userInfo;

    private JComboBox<String> businessComboBox;
    private JTextField locationField, industryField, amountField;

    public Business(UserInfo userInfo) {
        this.userInfo = userInfo;
        setTitle("DhanVriddhi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0xA5DD9B));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        headingLabel = new JLabel("DhanVriddhi");
        headingLabel.setFont(headingLabel.getFont().deriveFont(48.0f));
        mainPanel.add(headingLabel, gbc);

        gbc.gridy++;
        JLabel newInvestmentLabel = new JLabel("Start a New Investment:");
        newInvestmentLabel.setFont(newInvestmentLabel.getFont().deriveFont(18.0f));
        mainPanel.add(newInvestmentLabel, gbc);

        gbc.gridy++;
        JLabel businessLabel = new JLabel("Business:");
        mainPanel.add(businessLabel, gbc);

        gbc.gridx = 1;
        String[] businessOptions = {"Dairy", "Poultry", "Food Court", "Franchise"};
        businessComboBox = new JComboBox<>(businessOptions);
        mainPanel.add(businessComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel locationLabel = new JLabel("Location:");
        mainPanel.add(locationLabel, gbc);

        gbc.gridx = 1;
        locationField = new JTextField(15);
        mainPanel.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel industryLabel = new JLabel("Industry:");
        mainPanel.add(industryLabel, gbc);

        gbc.gridx = 1;
        industryField = new JTextField(15);
        mainPanel.add(industryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel amountLabel = new JLabel("Investment Amount:");
        mainPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        mainPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newInvestmentButton = new JButton("Create Firm");
        newInvestmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewInvestment();
            }
        });
        buttonPanel.add(newInvestmentButton);
        mainPanel.add(buttonPanel, gbc);

        gbc.gridy++;
        goToMyInvestments = new JLabel("Join Business");
        goToMyInvestments.setFont(goToMyInvestments.getFont().deriveFont(Font.BOLD, 14.0f));
        goToMyInvestments.setForeground(Color.BLUE);
        goToMyInvestments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToMyInvestments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	int userid=getUserId();
            	ExistingTeamPage existingTeamPage = new ExistingTeamPage(userid);
                existingTeamPage.setVisible(true);
                
            }
        });
        mainPanel.add(goToMyInvestments, gbc);
        gbc.gridy++;
        goToMyTeams = new JLabel("Go To My Teams");
        goToMyTeams.setFont(goToMyTeams.getFont().deriveFont(Font.BOLD, 14.0f));
        goToMyTeams.setForeground(Color.BLUE);
        goToMyTeams.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToMyTeams.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	int userid=getUserId();
            	UserTeamsPage userteam = new UserTeamsPage(userid);
                userteam.setVisible(true);
                
            }
        });
        
        mainPanel.add(goToMyTeams, gbc);
        gbc.gridy++;
        userDash = new JLabel("User Dashboard");
        userDash.setFont(userDash.getFont().deriveFont(Font.BOLD, 14.0f));
        userDash.setForeground(Color.BLUE);
        userDash.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userDash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	int userid=getUserId();
            	UserDashboard userdash = new UserDashboard(userid);
                userdash.setVisible(true);
                
            }
        });
        
        mainPanel.add(userDash, gbc);
        gbc.gridy++;
        LeaderBoard = new JLabel("Leader Board");
        LeaderBoard.setFont(userDash.getFont().deriveFont(Font.BOLD, 14.0f));
        LeaderBoard.setForeground(Color.BLUE);
        LeaderBoard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        LeaderBoard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	new EarningsLeaderboard();
                
            }
        });
        
        mainPanel.add(LeaderBoard, gbc);

        add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
   

    private void startNewInvestment() {
        String selectedBusiness = (String) businessComboBox.getSelectedItem();
        String location = locationField.getText();
        String industry = industryField.getText();
        String amountText = amountField.getText();
        
        BusinessInfo businessInfo = fetchUserAndBusinessIds();
        int retreuserId = businessInfo.getUserId();
        int retrebusinessId = businessInfo.getBusinessId();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the investment amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid investment amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (amount < 100000) {
            JOptionPane.showMessageDialog(this, "Investment amount must be greater than or equal to 100000.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert into business table with admin_id = 1
        int businessId = insertIntoBusinessTable(selectedBusiness, location, industry);
        if (businessId == -1) {
            JOptionPane.showMessageDialog(this, "Failed to insert into business table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get user ID from email
        int userId = getUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "User not found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert into investment table
        Connection conn = null;
        PreparedStatement insertInvestmentStatement = null;
        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            conn = DriverManager.getConnection(url, userName, passworddb);

            String query = "INSERT INTO investment (user_id, business_id, amount) VALUES (?, ?, ?)";
            insertInvestmentStatement = conn.prepareStatement(query);
            insertInvestmentStatement.setInt(1, userId);
            insertInvestmentStatement.setInt(2, businessId);
            insertInvestmentStatement.setDouble(3, amount);
            int investmentRowsInserted = insertInvestmentStatement.executeUpdate();
            if (investmentRowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "New investment record added successfully!");
                new TeamPage(userId, businessId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add new investment record. Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (insertInvestmentStatement != null) {
                    insertInvestmentStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int insertIntoBusinessTable(String businessName, String location, String industry) {
        int businessId = -1;
        Connection conn = null;
        PreparedStatement insertBusinessStatement = null;
        ResultSet generatedKeys = null;

        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            conn = DriverManager.getConnection(url, userName, passworddb);
            
            String selectAdminQuery = "SELECT admin_id FROM admin WHERE domain = ?";
            PreparedStatement selectAdminStatement = conn.prepareStatement(selectAdminQuery);
            selectAdminStatement.setString(1, businessName.toLowerCase());
            ResultSet adminResultSet = selectAdminStatement.executeQuery();
            int adminId = -1;
            if (adminResultSet.next()) {
                adminId = adminResultSet.getInt("admin_id");
            }

            if (adminId == -1) {
                throw new SQLException("Admin not found for the given business domain.");
            }

            String query = "INSERT INTO business (name, location, industry, admin_id) VALUES (?, ?, ?, ?)";
            insertBusinessStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            insertBusinessStatement.setString(1, businessName);
            insertBusinessStatement.setString(2, location);
            insertBusinessStatement.setString(3, industry);
            insertBusinessStatement.setInt(4, adminId);
            int rowsInserted = insertBusinessStatement.executeUpdate();

            if (rowsInserted > 0) {
                generatedKeys = insertBusinessStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    businessId = generatedKeys.getInt(1); // Get auto-generated business_id
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (insertBusinessStatement != null) {
                    insertBusinessStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return businessId;
    }

    private int getUserId() {
        int userId = -1;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            conn = DriverManager.getConnection(url, userName, passworddb);

            String query = "SELECT user_id FROM user WHERE email = ?";
            statement = conn.prepareStatement(query);
            statement.setString(1, userInfo.getEmail());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return userId;
    }
    
    private BusinessInfo fetchUserAndBusinessIds() {
        BusinessInfo businessInfo = new BusinessInfo();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            conn = DriverManager.getConnection(url, userName, passworddb);

            // Query to get user_id
            String userQuery = "SELECT user_id FROM user WHERE email = ?";
            statement = conn.prepareStatement(userQuery);
            statement.setString(1, userInfo.getEmail());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                businessInfo.setUserId(resultSet.getInt("user_id"));
            }

            // Query to get business_id
            String businessQuery = "SELECT business_id FROM business WHERE name = ?";
            statement = conn.prepareStatement(businessQuery);
            statement.setString(1, (String) businessComboBox.getSelectedItem());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                businessInfo.setBusinessId(resultSet.getInt("business_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return businessInfo;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Simulating the user login process
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail("test@example.com"); // Set the user's email (replace with actual login mechanism)
            new Business(userInfo);
        });
    }
}
