package myPackage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BusinessDashboard extends JFrame {
    private int businessId;
    private Connection conn;

    public BusinessDashboard(int businessId) {
        this.businessId = businessId;
        initializeDBConnection();
        setTitle("Business Dashboard - DhanVriddhi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Adding charts
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Net Profits", createBarChartPanel());
        tabbedPane.add("Share Distribution", createPieChartPanel());
        tabbedPane.add("Revenue and Expenses", createLineChartPanel());

        add(tabbedPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeDBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/sakethdb";
            String userName = "root";
            String passworddb = "saketh21@vce";
            conn = DriverManager.getConnection(url, userName, passworddb);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            String query = "SELECT date, (revenue - expenses) AS net_profit FROM business_data WHERE business_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, businessId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString("date");
                double netProfit = resultSet.getDouble("net_profit");
                dataset.addValue(netProfit, "Net Profit", date);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Net Profits Over Time",
                "Date",
                "Net Profit",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        return new ChartPanel(barChart);
    }

    private JPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            String query = "SELECT u.fn, u.ln, i.share FROM user u JOIN investment i ON u.user_id = i.user_id WHERE i.business_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, businessId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userName = resultSet.getString("fn") + " " + resultSet.getString("ln");
                double share = resultSet.getDouble("share");
                dataset.setValue(userName, share);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Share Distribution in Business",
                dataset,
                true, true, false);

        return new ChartPanel(pieChart);
    }

    private JPanel createLineChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            String query = "SELECT date, revenue, expenses FROM business_data WHERE business_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, businessId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString("date");
                double revenue = resultSet.getDouble("revenue");
                double expenses = resultSet.getDouble("expenses");
                dataset.addValue(revenue, "Revenue", date);
                dataset.addValue(expenses, "Expenses", date);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Revenue and Expenses Over Time",
                "Date",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        return new ChartPanel(lineChart);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int businessId = 1; // Example business ID
            new BusinessDashboard(businessId);
        });
    }
}
