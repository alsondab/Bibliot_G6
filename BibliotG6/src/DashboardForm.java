import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class DashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JButton btnRegister;
    private JLabel ibAdmin;
    private JPanel panelPieChart;
    private JTable table1;
    private JLabel tfManageBooks;
    private JPanel jpanel7;
    private JLabel tfManageUsers;
    private JLabel tfIssueBooks;
    private JLabel tfReturnBooks;
    private JLabel tfViewRecords;
    private JLabel tfViewIssueBooks;
    private JLabel tfDefaulterList;
    private JLabel tfLogout;
    private JLabel tfDashboard;
    private JLabel tfHomePage;
    private JLabel tfFeatures;
    private JTable table2;
    private JLabel CardsBooks;
    private JLabel CardsIssueBooks;
    private JLabel CardsUser;
    private JLabel CardsDefaulterList;
    private JLabel tfNotify;
    private PreparedStatement pst;
    private Connection conn;
    private Admins authenticatedAdmins;

    private Color originalColor;
    private final Color hoverColor = new Color(173, 216, 230); // Light blue color for hover effect

    public DashboardForm(Admins admins) {
        this.authenticatedAdmins = admins;
        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Establish and keep a persistent database connection
        connectToDatabase();
        updateDashboardCounts();


        // Check if there are any users in the database
        if (!checkIfAdminsExist()) {
            // If no users exist, redirect to SignupPage
            JOptionPane.showMessageDialog(this,
                    "No users found. Redirecting to SignupPage.",
                    "Signup Required",
                    JOptionPane.INFORMATION_MESSAGE);
            SignupPage signupPage = new SignupPage(this);
            Admins newAdmins = signupPage.admins;
            if (newAdmins != null) {
                JOptionPane.showMessageDialog(this,
                        "New User: " + newAdmins.name,
                        "Successful Registration",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUserDetailsTable(); // Load user details into table1
            }
        }

        btnRegister.addActionListener(e -> {
            SignupPage signupPage = new SignupPage(DashboardForm.this);
            Admins newAdmins = signupPage.admins;
            if (newAdmins != null) {
                JOptionPane.showMessageDialog(DashboardForm.this,
                        "New Admin: " + newAdmins.name,
                        "Successful Registration",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUserDetailsTable(); // Load user details into table1
            }
        });

        showPieChart();
        loadUserDetailsTable(); // Load user details into table1
        loadBookDetailsTable(); // Load book details into table2

        // Save the original color of the panel
        originalColor = jpanel7.getBackground();

        // Apply hover effect to all required labels
        addHoverEffect(tfManageBooks);
        addHoverEffect(tfManageUsers);
        addHoverEffect(tfIssueBooks);
        addHoverEffect(tfReturnBooks);
        addHoverEffect(tfViewRecords);
        addHoverEffect(tfViewIssueBooks);
        addHoverEffect(tfDefaulterList);
        addHoverEffect(tfNotify);
        addHoverEffect(tfLogout);
        addHoverEffect(tfDashboard);
        addHoverEffect(tfHomePage);
        addHoverEffect(tfFeatures);


        // Set ibAdmin label text to the name of the authenticated user
        ibAdmin.setText("Welcome, " + authenticatedAdmins.getName());

        // Add a mouse click listener to tfManageBooks to open ManageBooks window
        tfManageBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame manageBooksFrame = new JFrame("Manage Books");
                ManageBooks manageBooks = new ManageBooks(manageBooksFrame, DashboardForm.this);
                manageBooksFrame.setContentPane(manageBooks.getPanel1());
                manageBooksFrame.pack();
                manageBooksFrame.setLocationRelativeTo(null); // Center the frame
                manageBooksFrame.setVisible(true);
            }
        });

        // Add a mouse click listener to tfManageUsers to open ManageUser window
        tfManageUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame manageUserFrame = new JFrame("Manage Users");
                ManageUser manageUser = new ManageUser(manageUserFrame, DashboardForm.this);
                manageUserFrame.setContentPane(manageUser.getPanel1());
                manageUserFrame.pack();
                manageUserFrame.setLocationRelativeTo(null); // Center the frame
                manageUserFrame.setVisible(true);
                dispose();
            }
        });

        // Add a mouse click listener to tfIssueBooks to open IssueBooks window
        tfIssueBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame issueBooksFrame = new JFrame("Issue Books");
                IssueBooks issueBooks = new IssueBooks(issueBooksFrame, DashboardForm.this);
                issueBooksFrame.setContentPane(issueBooks.getPanelMain());
                issueBooksFrame.pack();
                issueBooksFrame.setLocationRelativeTo(null); // Center the frame
                issueBooksFrame.setVisible(true);
                // Hide the DashboardForm
            }
        });

        // Add a mouse click listener to tfReturnBooks to open ReturnBooks window
        tfReturnBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame returnBooksFrame = new JFrame("Return Books");
                ReturnBooks returnBooks = new ReturnBooks(DashboardForm.this);
                returnBooksFrame.setContentPane(returnBooks.getReturnBooksPanel()); // Use getReturnBooksPanel() method
                returnBooksFrame.pack();
                returnBooksFrame.setLocationRelativeTo(null); // Center the frame
                returnBooksFrame.setVisible(true);
                // Hide the DashboardForm
            }
        });

        // Add a mouse click listener to tfViewRecords to open ViewRecords window
        tfViewRecords.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame viewRecordsFrame = new JFrame("View Records");
                ViewRecords viewRecords = new ViewRecords(DashboardForm.this);
                viewRecordsFrame.setContentPane(viewRecords.getViewRecordsPanel());
                viewRecordsFrame.pack();
                viewRecordsFrame.setLocationRelativeTo(null); // Center the frame
                viewRecordsFrame.setVisible(true);
                // Hide the DashboardForm
            }
        });

        // Add a mouse click listener to tfViewIssueBooks to open ViewIssueBooks window
        tfViewIssueBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ViewIssueBooks viewIssueBooks = new ViewIssueBooks(DashboardForm.this);
                viewIssueBooks.setVisible(true);
                // Hide the DashboardForm
            }
        });

        // Add a mouse click listener to tfDefaulterList to open DefaulterList window
        tfDefaulterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaulterList defaulterList = new DefaulterList(DashboardForm.this);
                defaulterList.setVisible(true);
            }
        });
        // Add a mouse click listener to tfNotify to open NotifyUsers window
        tfNotify.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NotifyUsers notifyUsers = new NotifyUsers(DashboardForm.this, DashboardForm.this, conn);
                notifyUsers.setVisible(true);
            }
        });

        tfLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.exit(0);
            }
        });
       
    }

    private void addHoverEffect(JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(hoverColor);
                label.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setBackground(originalColor);
                label.setOpaque(false);
            }
        });
    }

    public void loadUserDetailsTable() {

        String query = "SELECT id, name, email, phone, address FROM Users";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            model.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table1.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load user details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public void loadBookDetailsTable() {
        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM bookdetails");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
            table2.setModel(tableModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while loading the book details table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
        String user = "root";
        String password = "";
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean checkIfAdminsExist() {
        boolean adminsExist = false;
        try {
            String query = "SELECT COUNT(*) FROM Admins";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                adminsExist = count > 0;
                //System.out.println("Number of admins: " + count); // Log the count of admins
            }
            pst.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminsExist;
    }

    private void updateDashboardCounts() {
        updateBooksCount();
        updateIssueBooksCount();
        updateUserCount();
        updateDefaulterListCount();
        showPieChart();
    }


    private void updateBooksCount() {
        String query = "SELECT COUNT(*) FROM BookDetails";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                CardsBooks.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch books count: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void updateIssueBooksCount() {
        String query = "SELECT COUNT(*) FROM IssueBooksDetails";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                CardsIssueBooks.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch issue books count: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    void updateUserCount() {
        String query = "SELECT COUNT(*) FROM Users";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                CardsUser.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch users count: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    void updateDefaulterListCount() {
        String query = "SELECT COUNT(*) FROM IssueBooksDetails WHERE Status = 'Pending' AND DueDate < CURRENT_DATE()";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                CardsDefaulterList.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch defaulter list count: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void showPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Fetch book names and their corresponding quantities from the database
        String query = "SELECT ibd.BookName, COUNT(ibd.BookId) AS IssueCount " +
                "FROM IssueBooksDetails ibd " +
                "GROUP BY ibd.BookId";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            // Add data to the dataset
            while (rs.next()) {
                String bookName = rs.getString("BookName");
                int issueCount = rs.getInt("IssueCount");
                dataset.setValue(bookName, issueCount);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch data for Pie Chart: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Create the Pie Chart
        JFreeChart pieChart = ChartFactory.createPieChart("Book Issuance", dataset, true, true, false);

        // Customize the appearance of the chart if needed
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.white);

        // Create a ChartPanel and add it to the panelPieChart
        ChartPanel chartPanel = new ChartPanel(pieChart);
        panelPieChart.removeAll();
        panelPieChart.add(chartPanel, BorderLayout.CENTER);
        panelPieChart.validate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginForm = new LoginPage(null);
            Admins admins = loginForm.admins;
            if (admins != null) {
                DashboardForm dashboardForm = new DashboardForm(admins);
                dashboardForm.setVisible(true);
            } else {
                // Check if there are any admins in the database
                if (!checkIfAdminsExistInDatabase()) {
                    JOptionPane.showMessageDialog(null, "No admins found. Redirecting to SignupPage.");
                    SignupPage signupPage = new SignupPage(null);
                    Admins newAdmin = signupPage.admins;
                    if (newAdmin != null) {
                        DashboardForm dashboardForm = new DashboardForm(newAdmin);
                        dashboardForm.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to register new admin. Exiting application.");
                        System.exit(0);
                    }
                } else {
                    // Open LoginPage if admins exist in the database
                    JOptionPane.showMessageDialog(null, "Please login to continue.");
                    LoginPage loginPage = new LoginPage(null);
                    Admins authenticatedAdmin = loginPage.admins;
                    if (authenticatedAdmin != null) {
                        DashboardForm dashboardForm = new DashboardForm(authenticatedAdmin);
                        dashboardForm.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Authentication failed. Exiting application.");
                        System.exit(0);
                    }
                }
            }
        });
    }

    private static boolean checkIfAdminsExistInDatabase() {
        boolean adminsExist = false;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC", "root", "");
             PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM Admins");
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                adminsExist = count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminsExist;
    }

    public Connection getConnection() {
        return conn;
    }
    public JPanel getPanelMain() {
        return dashboardPanel;
    }
}