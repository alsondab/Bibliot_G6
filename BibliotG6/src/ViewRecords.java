import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Date;

public class ViewRecords {
    private rojeru_san.componentes.RSDateChooser tfIssueDate;
    private rojeru_san.componentes.RSDateChooser tfIssueDue;
    private JButton tfIssueSearch;
    private JTable table1;
    private JLabel btnBack;
    private JPanel viewRecordsPanel;
    private DashboardForm dashboardForm;
    private Connection conn;

    public ViewRecords(DashboardForm dashboardForm) {
        this.dashboardForm = dashboardForm;
        conn = dashboardForm.getConnection();

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dashboardForm.setVisible(true); // Show the DashboardForm
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(viewRecordsPanel);
                topFrame.dispose(); // Close the ViewRecords window
            }
        });

        tfIssueSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fetchIssueRecords();
            }
        });

        // Fetch all records when ViewRecords window is opened
        fetchAllIssueRecords();
    }

    private void fetchAllIssueRecords() {
        try {
            String query = "SELECT id, BookName, userName, issueDate, dueDate, status FROM IssueBooksDetails";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            displayResultSet(rs);
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching issue records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void fetchIssueRecords() {
        // Retrieve selected issue date and due date
        Date issueDate = tfIssueDate.getDatoFecha();
        Date dueDate = tfIssueDue.getDatoFecha();

        // Fetch records from the database based on selected dates and display in table1
        try {
            String query = "SELECT id, BookName, userName, issueDate, dueDate, status FROM IssueBooksDetails " +
                    "WHERE (issueDate BETWEEN ? AND ?) OR (dueDate BETWEEN ? AND ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setDate(1, new java.sql.Date(issueDate.getTime()));
            pst.setDate(2, new java.sql.Date(dueDate.getTime()));
            pst.setDate(3, new java.sql.Date(issueDate.getTime()));
            pst.setDate(4, new java.sql.Date(dueDate.getTime()));
            ResultSet rs = pst.executeQuery();

            if (!rs.isBeforeFirst()) { // Check if the result set is empty
                JOptionPane.showMessageDialog(null, "No records found matching the selected dates.", "No Match", JOptionPane.INFORMATION_MESSAGE);
                fetchAllIssueRecords(); // Reset the table to show all records
            } else {
                displayResultSet(rs);
            }
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching issue records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void displayResultSet(ResultSet rs) throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Add custom column headers
        String[] columnHeaders = {"ID", "Book Name", "User Name", "Issue Date", "Due Date", "Status"};
        model.setColumnIdentifiers(columnHeaders);

        // Add rows to the model
        while (rs.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = rs.getObject(i);
            }
            model.addRow(rowData);
        }

        // Set the model to table1
        table1.setModel(model);
        rs.close();
    }

    public JPanel getViewRecordsPanel() {
        return viewRecordsPanel;
    }
}
