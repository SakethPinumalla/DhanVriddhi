package myPackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserTeamsPage extends JFrame {

    private int userId;

    public UserTeamsPage(int userId) {
        this.userId = userId;
        setTitle("User Teams");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        fetchAndDisplayTeams();
    }

    private void fetchAndDisplayTeams() {
        try {
            // Establish database connection
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String password = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, password);

            // Prepare and execute SQL query to fetch team details for the given user ID
            String query = "SELECT * FROM team WHERE team_id IN (SELECT team_id FROM team_user WHERE user_id = ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            // Iterate over the result set and create panels for each team
            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String teamName = resultSet.getString("team_name");

                JPanel teamPanel = new JPanel();
                teamPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                teamPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel nameLabel = new JLabel("Team Name: " + teamName);
                teamPanel.add(nameLabel);

                JButton teamPageButton = new JButton("Go to Team Page");
                teamPageButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Open team page for the selected team
                        openTeamPage(teamId);
                    }
                });
                teamPanel.add(teamPageButton);

                add(teamPanel);
            }

            // Close resources
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch teams. Error: " + ex.getMessage());
        }

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void openTeamPage(int teamId) {
        new TeamInterfacePage(teamId,userId);
    }

    public static void main(String[] args) {
        // Example usage
        SwingUtilities.invokeLater(() -> {
            new UserTeamsPage(123); // Pass the user ID here
        });
    }
}
