package myPackage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class TeamChatUI extends JFrame {
    private JEditorPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private int teamId;
    private int userId;

    public TeamChatUI(int teamId, int userId) {
        this.teamId = teamId;
        this.userId = userId;

        setTitle("Team Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        chatArea = new JEditorPane();
        chatArea.setContentType("text/html");
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(240, 240, 240));
        chatArea.setBorder(new LineBorder(Color.GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(100, 149, 237));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(80, messageField.getPreferredSize().height)); // Set button size
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        sendButton.addActionListener(e -> sendMessage());

        // Load messages when initializing the UI
        loadMessages();
    }

    private void loadMessages() {
        StringBuilder htmlContent = new StringBuilder("<html><body style='font-family:Arial;'>");
        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String dbUserName = "root";
            String dbPassword = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, dbUserName, dbPassword);

            String query = "SELECT tc.message, tc.timestamp, u.fn, u.ln FROM team_chat tc " +
                           "JOIN user u ON tc.user_id = u.user_id " +
                           "WHERE tc.team_id = ? ORDER BY tc.timestamp";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, teamId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                String timestamp = resultSet.getString("timestamp");
                String user = resultSet.getString("fn") + " " + resultSet.getString("ln");
                htmlContent.append("<p><b style='font-size:14px;color:#3333FF;'>" + user + ":</b> " + message + " <i style='font-size:10px;color:#808080;'>(" + timestamp + ")</i></p>");
            }

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load messages. Error: " + ex.getMessage());
        }
        htmlContent.append("</body></html>");
        chatArea.setText(htmlContent.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                String url = "jdbc:mysql://localhost:3306/sakethdb";
                String dbUserName = "root";
                String dbPassword = "saketh21@vce";
                Connection conn = DriverManager.getConnection(url, dbUserName, dbPassword);

                String query = "INSERT INTO team_chat (message, team_id, user_id, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, message);
                statement.setInt(2, teamId);
                statement.setInt(3, userId);

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    loadMessages(); // Reload messages to update the chat area
                    messageField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send message.");
                }

                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to send message. Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TeamChatUI(1, 4)); // Pass teamId and userId as parameters
    }
}
