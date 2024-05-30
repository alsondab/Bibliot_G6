import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ManageBooks {
    private JPanel panel1;
    private JLabel btnBack;
    private JButton btnAdd;
    private JTextField tfBookName;
    private JTextField tfAuthorName;
    private JTextField tfQuantity;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTable table2;
    private JFrame frame; // Reference to the current JFrame
    private DashboardForm dashboardForm; // Reference to the DashboardForm
    private Connection conn;

    public ManageBooks(JFrame frame, DashboardForm dashboardForm) {
        this.frame = frame;
        this.dashboardForm = dashboardForm;
        this.conn = conn;

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dashboardForm.setVisible(true);
                frame.dispose();
            }
        });

        // Load data into table
        table_load();

        // Add mouse click listener to table2
        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table2.getSelectedRow();
                if (row != -1) { // Ensure a row is selected
                    tfBookName.setText(table2.getValueAt(row, 1).toString());
                    tfAuthorName.setText(table2.getValueAt(row, 2).toString());
                    tfQuantity.setText(table2.getValueAt(row, 3).toString());
                }
            }
        });

        // Add button listener for Add button
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
                dashboardForm.loadBookDetailsTable(); // Reload book details in DashboardForm
            }
        });

        // Add button listener for Update button
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
                dashboardForm.loadBookDetailsTable(); // Reload book details in DashboardForm
            }
        });

        // Add button listener for Delete button
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
                dashboardForm.loadBookDetailsTable(); // Reload book details in DashboardForm
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }

    void table_load() {
        try {
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
            JOptionPane.showMessageDialog(null, "An error occurred while loading the table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addBook() {
        try {
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }

            String sql = "INSERT INTO bookdetails (BookName, Authors, Quantity) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tfBookName.getText());
            pst.setString(2, tfAuthorName.getText());
            pst.setString(3, tfQuantity.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book Added Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while adding the Book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateBook() {
        try {
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }

            int selectedRow = table2.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a Book to update");
                return;
            }

            String selectedBookId = table2.getValueAt(selectedRow, 0).toString();
            String sql = "UPDATE bookdetails SET BookName = ?, Authors = ?, Quantity = ? WHERE BookId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tfBookName.getText());
            pst.setString(2, tfAuthorName.getText());
            pst.setString(3, tfQuantity.getText());
            pst.setString(4, selectedBookId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book Updated Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while updating the Book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        try {
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
            int selectedRow = table2.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a Book to delete");
                return;
            }
            String bookId = table2.getValueAt(selectedRow, 0).toString();
            String sql = "DELETE FROM bookdetails WHERE BookId = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book Deleted Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the Book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tfBookName.setText("");
        tfAuthorName.setText("");
        tfQuantity.setText("");
    }
}
