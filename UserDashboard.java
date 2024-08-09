package myPackage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;

public class UserDashboard extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sakethdb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "saketh21@vce";
    private static final int PROFILE_PIC_WIDTH = 150;
    private static final int PROFILE_PIC_HEIGHT = 150;

    private int userId;
    private Connection conn;
    private DefaultCategoryDataset lineDataset;
    private DefaultPieDataset pieDataset;
    private CardLayout cardLayout;
    private JPanel chartPanelContainer;

    public UserDashboard(int userId) {
        this.userId = userId;
        setTitle("User Dashboard - DhanVriddhi");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the connection
        initializeDBConnection();

        // Fetch and set data
        lineDataset = new DefaultCategoryDataset();
        pieDataset = new DefaultPieDataset();
        fetchDataForCharts();

        // Create the panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel userDetailsPanel = createUserDetailsPanel();
        JPanel chartSelectionPanel = createChartSelectionPanel();

        cardLayout = new CardLayout();
        chartPanelContainer = new JPanel(cardLayout);
        JPanel lineChartPanel = createLineChartPanel();
        JPanel pieChartPanel = createPieChartPanel();

        chartPanelContainer.add(lineChartPanel, "Line Chart");
        chartPanelContainer.add(pieChartPanel, "Pie Chart");

        mainPanel.add(userDetailsPanel, BorderLayout.NORTH);
        mainPanel.add(chartSelectionPanel, BorderLayout.CENTER);
        mainPanel.add(chartPanelContainer, BorderLayout.SOUTH);

        add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeDBConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fetchDataForCharts() {
        fetchLineChartData();
        fetchPieChartData();
    }

    private void fetchLineChartData() {
        String query = "SELECT date, revenue, expenses FROM business_data bd JOIN investment i ON bd.business_id = i.business_id WHERE i.user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    double revenue = resultSet.getDouble("revenue");
                    double expenses = resultSet.getDouble("expenses");
                    lineDataset.addValue(revenue, "Revenue", date);
                    lineDataset.addValue(expenses, "Expenses", date);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fetchPieChartData() {
        String query = "SELECT b.name, i.amount FROM business b JOIN investment i ON b.business_id = i.business_id WHERE i.user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String businessName = resultSet.getString("name");
                    double amount = resultSet.getDouble("amount");
                    pieDataset.setValue(businessName, amount);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createUserDetailsPanel() {
        JPanel userDetailsPanel = new JPanel(new BorderLayout());
        userDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel profilePicPanel = new JPanel(new BorderLayout());
        JLabel profilePicLabel = new JLabel();
        profilePicPanel.add(profilePicLabel, BorderLayout.CENTER);
        userDetailsPanel.add(profilePicPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel earningsLabel = new JLabel();
        JLabel investmentsLabel = new JLabel();
        infoPanel.add(earningsLabel);
        infoPanel.add(investmentsLabel);
        userDetailsPanel.add(infoPanel, BorderLayout.CENTER);

        fetchUserDetails(earningsLabel, investmentsLabel, profilePicLabel);

        return userDetailsPanel;
    }

    private void fetchUserDetails(JLabel earningsLabel, JLabel investmentsLabel, JLabel profilePicLabel) {
        String query = "SELECT earnings, investments, profile_picture FROM user WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double earnings = resultSet.getDouble("earnings");
                    double investments = resultSet.getDouble("investments");
                    byte[] profilePicture = resultSet.getBytes("profile_picture");

                    earningsLabel.setText("Earnings: ₹" + earnings);
                    investmentsLabel.setText("Investments: ₹" + investments);

                    if (profilePicture != null) {
                        try {
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(profilePicture));
                            Image scaledImg = img.getScaledInstance(PROFILE_PIC_WIDTH, PROFILE_PIC_HEIGHT, Image.SCALE_SMOOTH);
                            ImageIcon profilePicIcon = new ImageIcon(scaledImg);
                            profilePicLabel.setIcon(profilePicIcon);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        profilePicLabel.setText("No Profile Picture");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createChartSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton lineChartButton = new JButton("View Line Chart");
        JButton pieChartButton = new JButton("View Pie Chart");

        lineChartButton.addActionListener(e -> cardLayout.show(chartPanelContainer, "Line Chart"));
        pieChartButton.addActionListener(e -> cardLayout.show(chartPanelContainer, "Pie Chart"));

        panel.add(lineChartButton);
        panel.add(pieChartButton);

        return panel;
    }

    private JPanel createLineChartPanel() {
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Business Revenue and Expenses",
                "Date",
                "Amount",
                lineDataset
        );

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        return chartPanel;
    }

    private JPanel createPieChartPanel() {
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Investment Distribution",
                pieDataset,
                true,
                true,
                false
        );

        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(800, 400));

        return pieChartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserDashboard(1);
        });
    }
}
