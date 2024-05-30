import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ViewIssueBooks extends JFrame {
    private JLabel btnBack;
    private JTable table1;
    private JPanel ViewIssueBooksPanel;
    private Connection conn;

    public ViewIssueBooks(DashboardForm dashboardForm) {
        setTitle("View Issued Books");
        setContentPane(ViewIssueBooksPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.conn = dashboardForm.getConnection(); // Use the existing connection from DashboardForm

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dashboardForm.setVisible(true);
                dispose(); // Close the current window
            }
        });

        loadIssueBooksTable(); // Load data into the table
    }

    private void loadIssueBooksTable() {
        String query = "SELECT BookId, BookName, userId, UserName, issueDate, dueDate, status FROM IssueBooksDetails WHERE Status = 'pending'";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            // Get metadata for column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create a table model and set Column Identifiers to this model
            DefaultTableModel model = new DefaultTableModel();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            model.setColumnIdentifiers(columnNames);

            // Add rows to the model
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table1.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load issue book details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public JPanel getViewIssueBooksPanel() {
        return ViewIssueBooksPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewIssueBooks viewIssueBooks = new ViewIssueBooks(null);
            viewIssueBooks.setVisible(true);
        });
    }
}
