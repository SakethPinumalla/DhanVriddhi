package myPackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TeamPage extends JFrame {
    private JButton createNewTeamButton;
    private int userId;
    private int businessId;

    public TeamPage(int userId, int businessId) {
        this.userId = userId;
        this.businessId = businessId;
        
        setTitle("Team Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0xA5DD9B));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Heading Label
        JLabel headingLabel = new JLabel("Team Page");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(headingLabel, gbc);

        // Create New Team Button
        createNewTeamButton = new JButton("Create New Team");
        createNewTeamButton.setFont(new Font("Arial", Font.PLAIN, 16));
        createNewTeamButton.setPreferredSize(new Dimension(200, 50));
        createNewTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateTeamForm();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createNewTeamButton, gbc);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
   
    private void openCreateTeamForm() {
        JTextField teamNameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Team Name:"));
        panel.add(teamNameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScrollPane);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Team", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String teamName = teamNameField.getText();
            String description = descriptionArea.getText();

            // Insert team data into the database
            try {
                String url = "jdbc:mysql://localhost:3306/sakethdb";
                String userName = "root";
                String passworddb = "saketh21@vce";
                Connection conn = DriverManager.getConnection(url, userName, passworddb);

                String query = "INSERT INTO team (team_name, business_id, description, vacancy, total_profits, current_profits) VALUES (?, ?, ?, ?, 0, 0)";
                PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, teamName);
                statement.setInt(2, businessId); // Assuming businessId is available
                statement.setString(3, description);
                statement.setInt(4, 4); // Default vacancy is 4

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    // Get the generated team_id
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int teamId = generatedKeys.getInt(1);
                        // Insert into investment table
                        insertIntoTeamUserTable(teamId,userId);
                        JOptionPane.showMessageDialog(this, "Team created successfully!");
                        new TeamInterfacePage(teamId,userId);
                    }
                }

                statement.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to create team. Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to insert values into the investment table
    private void insertIntoTeamUserTable(int teamId, int userId) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakethdb";
        String userName = "root";
        String passworddb = "saketh21@vce";
        Connection conn = DriverManager.getConnection(url, userName, passworddb);

        String insertQuery = "INSERT INTO team_user (team_id, user_id) VALUES (?, ?)";
        PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
        insertStatement.setInt(1, teamId);
        insertStatement.setInt(2, userId);
        insertStatement.executeUpdate();

        conn.close();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Simulating the user login process
                int userId = 123; // Replace with actual user ID obtained from the Business class
                int businessId = 456; // Replace with actual business ID obtained from the Business class
                new TeamPage(userId, businessId);
            }
        });
    }
}
