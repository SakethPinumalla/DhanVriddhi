package myPackage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class EarningsLeaderboard extends JFrame {

    private JComboBox<String> filterOptions;
    private JPanel graphPanel;
    private JPanel tablePanel;

    public EarningsLeaderboard() {
        setTitle("Earnings Leaderboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Earnings Leaderboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        // Filter Options
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());

        filterOptions = new JComboBox<>(new String[]{"All Time", "Today"});
        JButton showUsersButton = new JButton("Show Users");
        JButton showTeamsButton = new JButton("Show Teams");

        filterPanel.add(new JLabel("Filter by:"));
        filterPanel.add(filterOptions);
        filterPanel.add(showUsersButton);
        filterPanel.add(showTeamsButton);

        add(filterPanel, BorderLayout.NORTH);

        // Panels for graph and table
        graphPanel = new JPanel();
        add(graphPanel, BorderLayout.CENTER);
        tablePanel = new JPanel(new BorderLayout());
        add(tablePanel, BorderLayout.SOUTH);

        // Action Listeners
        showUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUsersEarnings();
            }
        });

        showTeamsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTeamsEarnings();
            }
        });

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakethdb";
        String username = "root";
        String password = "saketh21@vce";
        return DriverManager.getConnection(url, username, password);
    }

    private void showUsersEarnings() {
        String filter = (String) filterOptions.getSelectedItem();
        String query;
        if (filter.equals("Today")) {
            query = "SELECT u.user_id, u.fn, u.ln, SUM((bd.revenue - bd.expenses) * i.share) AS earnings " +
                    "FROM user u " +
                    "JOIN investment i ON u.user_id = i.user_id " +
                    "JOIN business_data bd ON i.business_id = bd.business_id " +
                    "WHERE bd.date = ? " +
                    "GROUP BY u.user_id, u.fn, u.ln " +
                    "ORDER BY earnings DESC";
        } else {
            query = "SELECT u.user_id, u.fn, u.ln, SUM((bd.revenue - bd.expenses) * i.share) AS earnings " +
                    "FROM user u " +
                    "JOIN investment i ON u.user_id = i.user_id " +
                    "JOIN business_data bd ON i.business_id = bd.business_id " +
                    "GROUP BY u.user_id, u.fn, u.ln " +
                    "ORDER BY earnings DESC";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (filter.equals("Today")) {
                stmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            }

            ResultSet rs = stmt.executeQuery();
            Vector<Vector<Object>> data = new Vector<>();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            int count = 0;
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String fn = rs.getString("fn");
                String ln = rs.getString("ln");
                double earnings = rs.getDouble("earnings");

                if (count < 3) {
                    String position;
                    if (count == 0) position = "First";
                    else if (count == 1) position = "Second";
                    else position = "Third";
                    dataset.addValue(earnings, position, fn + " " + ln);
                } else {
                    Vector<Object> row = new Vector<>();
                    row.add(userId);
                    row.add(fn);
                    row.add(ln);
                    row.add(earnings);
                    data.add(row);
                }
                count++;
            }

            updateGraph(dataset);
            updateTable(data, new String[]{"User ID", "First Name", "Last Name", "Earnings"});

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTeamsEarnings() {
        String filter = (String) filterOptions.getSelectedItem();
        String query;
        if (filter.equals("Today")) {
            query = "SELECT t.team_id, t.team_name, SUM((bd.revenue - bd.expenses) * i.share) AS earnings " +
                    "FROM team t " +
                    "JOIN investment i ON t.business_id = i.business_id " +
                    "JOIN business_data bd ON i.business_id = bd.business_id " +
                    "WHERE bd.date = ? " +
                    "GROUP BY t.team_id, t.team_name " +
                    "ORDER BY earnings DESC";
        } else {
            query = "SELECT t.team_id, t.team_name, SUM((bd.revenue - bd.expenses) * i.share) AS earnings " +
                    "FROM team t " +
                    "JOIN investment i ON t.business_id = i.business_id " +
                    "JOIN business_data bd ON i.business_id = bd.business_id " +
                    "GROUP BY t.team_id, t.team_name " +
                    "ORDER BY earnings DESC";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (filter.equals("Today")) {
                stmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            }

            ResultSet rs = stmt.executeQuery();
            Vector<Vector<Object>> data = new Vector<>();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            int count = 0;
            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                double earnings = rs.getDouble("earnings");

                if (count < 3) {
                    String position;
                    if (count == 0) position = "First";
                    else if (count == 1) position = "Second";
                    else position = "Third";
                    dataset.addValue(earnings, position, teamName);
                } else {
                    Vector<Object> row = new Vector<>();
                    row.add(teamId);
                    row.add(teamName);
                    row.add(earnings);
                    data.add(row);
                }
                count++;
            }

            updateGraph(dataset);
            updateTable(data, new String[]{"Team ID", "Team Name", "Earnings"});

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateGraph(DefaultCategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 3 Standings",
                "",
                "Earnings",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.15);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(750, 300));
        graphPanel.removeAll();
        graphPanel.add(chartPanel);
        graphPanel.validate();
    }

    private void updateTable(Vector<Vector<Object>> data, String[] columnNames) {
        Vector<String> columnNamesVector = new Vector<>();
        for (String columnName : columnNames) {
            columnNamesVector.add(columnName);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNamesVector);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);

        if (columnNames.length > 3) {
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.removeAll();
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.validate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EarningsLeaderboard::new);
    }
}
