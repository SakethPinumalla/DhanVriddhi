package myPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TeamInterfacePage extends JFrame {

    private int teamId;
    private int userId;

    public TeamInterfacePage(int teamId,int userId) {
        this.teamId = teamId;
        this.userId=userId;
        setTitle("Team Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        displayTeamDetails();
    }

    private void displayTeamDetails() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0x7AB2B2)); // Set background color

        JPanel teamPanel = new JPanel(new BorderLayout());
        JPanel revenuePanel = new JPanel(new BorderLayout());

        // Team details table
        String[] teamColumnNames = {"Details", "Value"};
        DefaultTableModel teamModel = new DefaultTableModel(teamColumnNames, 0);
        JTable teamTable = new JTable(teamModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Revenue data table
        String[] revenueColumnNames = {"Date", "Revenue", "Expenses", "Net Profit"};
        DefaultTableModel revenueModel = new DefaultTableModel(revenueColumnNames, 0);
        JTable revenueTable = new JTable(revenueModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Center align cell contents
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        teamTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        teamTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        revenueTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        revenueTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        revenueTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        revenueTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        try {
            // Establish database connection
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String password = "saketh21@vce";
            Connection conn = DriverManager.getConnection(url, userName, password);

            // Query 1: Select business_id from team where team_id = ?
            String businessIdQuery = "SELECT business_id FROM team WHERE team_id = ?";
            PreparedStatement businessIdStmt = conn.prepareStatement(businessIdQuery);
            businessIdStmt.setInt(1, teamId);
            ResultSet businessIdResultSet = businessIdStmt.executeQuery();

            int businessId = -1;
            if (businessIdResultSet.next()) {
                businessId = businessIdResultSet.getInt("business_id");
            }

            businessIdResultSet.close();
            businessIdStmt.close();

            if (businessId == -1) {
                JOptionPane.showMessageDialog(this, "Business ID not found for the team!");
                return;
            }

            // Query 2: Total Investment
            String totalInvestmentQuery = "SELECT SUM(amount) AS totalInvestment FROM investment WHERE business_id = ?";
            PreparedStatement totalInvestmentStmt = conn.prepareStatement(totalInvestmentQuery);
            totalInvestmentStmt.setInt(1, businessId);
            ResultSet totalInvestmentResultSet = totalInvestmentStmt.executeQuery();

            double totalInvestment = 0;
            if (totalInvestmentResultSet.next()) {
                totalInvestment = totalInvestmentResultSet.getDouble("totalInvestment");
            }

            totalInvestmentResultSet.close();
            totalInvestmentStmt.close();

            if (totalInvestment == 0) {
                JOptionPane.showMessageDialog(this, "Total investment is zero!");
                return;
            }

            // Query 3: Calculate share for each user_id
            String userShareQuery = "SELECT user_id, amount FROM investment WHERE business_id = ?";
            PreparedStatement userShareStmt = conn.prepareStatement(userShareQuery);
            userShareStmt.setInt(1, businessId);
            ResultSet userShareResultSet = userShareStmt.executeQuery();

            String updateShareQuery = "UPDATE investment SET share = ? WHERE business_id = ? AND user_id = ?";
            PreparedStatement updateShareStmt = conn.prepareStatement(updateShareQuery);

            while (userShareResultSet.next()) {
                int userId = userShareResultSet.getInt("user_id");
                double amount = userShareResultSet.getDouble("amount");
                double share = amount / totalInvestment;

                updateShareStmt.setDouble(1, share);
                updateShareStmt.setInt(2, businessId);
                updateShareStmt.setInt(3, userId);
                updateShareStmt.executeUpdate();
            }

            userShareResultSet.close();
            updateShareStmt.close();

            // Fetch team details and associated business data
            String teamQuery = "SELECT t.team_name, t.description, t.total_profits, t.current_profits, " +
                    "SUM(bd.revenue) AS total_revenue, SUM(bd.expenses) AS total_expenses, " +
                    "SUM(bd.revenue - bd.expenses) AS total_net_profit, " +
                    "(SELECT SUM(bd2.revenue - bd2.expenses) FROM business_data bd2 " +
                    "WHERE bd2.business_id = t.business_id AND bd2.date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)) AS last_30_days_net_profit " +
                    "FROM team t " +
                    "LEFT JOIN business_data bd ON t.business_id = bd.business_id " +
                    "WHERE t.team_id = ?";
            PreparedStatement teamStatement = conn.prepareStatement(teamQuery);
            teamStatement.setInt(1, teamId);
            ResultSet teamResultSet = teamStatement.executeQuery();

            if (teamResultSet.next()) {
                String teamName = teamResultSet.getString("team_name");
                String description = teamResultSet.getString("description");
                double totalRevenue = teamResultSet.getDouble("total_revenue");
                double totalExpenses = teamResultSet.getDouble("total_expenses");
                double totalNetProfit = teamResultSet.getDouble("total_net_profit");
                double last30DaysNetProfit = teamResultSet.getDouble("last_30_days_net_profit");

                teamModel.addRow(new Object[]{"Team Name", teamName});
                teamModel.addRow(new Object[]{"Description", description});
                teamModel.addRow(new Object[]{"Total Revenue", String.format("₹%.2f", totalRevenue)});
                teamModel.addRow(new Object[]{"Total Expenses", String.format("₹%.2f", totalExpenses)});
                teamModel.addRow(new Object[]{"Total Net Profit", String.format("₹%.2f", totalNetProfit)});
                teamModel.addRow(new Object[]{"Last 30 Days Net Profit", String.format("₹%.2f", last30DaysNetProfit)});

                String updateQuery = "UPDATE team SET total_profits = ?, current_profits = ? WHERE team_id = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setDouble(1, totalNetProfit);
                updateStatement.setDouble(2, last30DaysNetProfit);
                updateStatement.setInt(3, teamId);
                updateStatement.executeUpdate();
                updateStatement.close();
            } else {
                JOptionPane.showMessageDialog(this, "Team not found!");
            }

            teamResultSet.close();
            teamStatement.close();

            // Fetch revenue data
            String revenueQuery = "SELECT date, revenue, expenses, (revenue - expenses) AS net_profit " +
                    "FROM business_data WHERE business_id = ?";
            PreparedStatement revenueStatement = conn.prepareStatement(revenueQuery);
            revenueStatement.setInt(1, businessId);
            ResultSet revenueResultSet = revenueStatement.executeQuery();

            while (revenueResultSet.next()) {
                String date = revenueResultSet.getString("date");
                double revenue = revenueResultSet.getDouble("revenue");
                double expenses = revenueResultSet.getDouble("expenses");
                double netProfit = revenueResultSet.getDouble("net_profit");

                revenueModel.addRow(new Object[]{date, String.format("₹%.2f", revenue),
                        String.format("₹%.2f", expenses), String.format("₹%.2f", netProfit)});

                // Update net profit for each user based on their share
                String getUserSharesQuery = "SELECT user_id, share FROM investment WHERE business_id = ?";
                PreparedStatement getUserSharesStmt = conn.prepareStatement(getUserSharesQuery);
                getUserSharesStmt.setInt(1, businessId);
                ResultSet userSharesResultSet = getUserSharesStmt.executeQuery();

                String updateEarningsQuery = "UPDATE user SET earnings = earnings + ? WHERE user_id = ?";
                PreparedStatement updateEarningsStmt = conn.prepareStatement(updateEarningsQuery);

                while (userSharesResultSet.next()) {
                    int userId = userSharesResultSet.getInt("user_id");
                    double share = userSharesResultSet.getDouble("share");
                    double userEarnings = share * netProfit;

                    updateEarningsStmt.setDouble(1, userEarnings);
                    updateEarningsStmt.setInt(2, userId);
                    updateEarningsStmt.executeUpdate();
                }

                userSharesResultSet.close();
                getUserSharesStmt.close();
                updateEarningsStmt.close();
            }

            revenueResultSet.close();
            revenueStatement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch team details. Error: " + ex.getMessage());
        }

        
        
        // Set table properties
        teamTable.setRowHeight(30);
        teamTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        teamTable.setFont(new Font("Arial", Font.PLAIN, 14));
        teamTable.setGridColor(Color.BLACK);
        teamTable.setShowGrid(true);
        teamTable.setFillsViewportHeight(true);

        revenueTable.setRowHeight(30);
        revenueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        revenueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        revenueTable.setGridColor(Color.BLACK);
        revenueTable.setShowGrid(true);
        revenueTable.setFillsViewportHeight(true);

        JScrollPane teamScrollPane = new JScrollPane(teamTable);
        JScrollPane revenueScrollPane = new JScrollPane(revenueTable);

        teamPanel.add(teamScrollPane, BorderLayout.CENTER);
        revenuePanel.add(revenueScrollPane, BorderLayout.CENTER);

        mainPanel.add(teamPanel, BorderLayout.NORTH);
        mainPanel.add(revenuePanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton Dashboard = new JButton("Dashboard");
        JButton teamChatButton = new JButton("Team-Chat");
        buttonsPanel.add(Dashboard);
        buttonsPanel.add(teamChatButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        Dashboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             int businessId=getBusinessIdFromTeamId(teamId);
                 new BusinessDashboard(businessId);
            }
        });

        teamChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TeamChatUI(teamId,userId);
            }
        });

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private int getBusinessIdFromTeamId(int teamId) {
        int businessId = -1;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
        	 String url = "jdbc:mysql://localhost:3306/sakethdb";
             String userName = "root";
             String password = "saketh21@vce";
             Connection conn = DriverManager.getConnection(url, userName, password);
             String query = "SELECT business_id FROM team WHERE team_id = ?";
             statement = conn.prepareStatement(query);
             statement.setInt(1, teamId);
             resultSet = statement.executeQuery();

            if (resultSet.next()) {
                businessId = resultSet.getInt("business_id");
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return businessId;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TeamInterfacePage(123,1));
    }
}
