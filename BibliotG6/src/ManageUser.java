import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ManageUser {
    private JPanel panel1;
    private JLabel btnBack;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JTextField tfPassword;
    private JTable table1;
    private JFrame frame; // Reference to the current JFrame
    private DashboardForm dashboardForm; // Reference to the DashboardForm
    private Connection conn;

    public ManageUser(JFrame frame, DashboardForm dashboardForm) {
        this.frame = frame;
        this.dashboardForm = dashboardForm;
        this.conn = conn;

        // Add a mouse click listener to btnBack to close ManageUser and show DashboardForm
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show the DashboardForm
                dashboardForm.setVisible(true);
                // Close the current frame (ManageUser)
                frame.dispose();
            }
        });

        // Fetch data and display it in table1
        table_load();

        // Add mouse click listener to table1
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table1.getSelectedRow();
                if (row != -1) { // Ensure a row is selected
                    // Get data from the selected row and populate text fields
                    tfName.setText(table1.getValueAt(row, 1).toString());
                    tfEmail.setText(table1.getValueAt(row, 2).toString());
                    tfPhone.setText(table1.getValueAt(row, 3).toString());
                    tfAddress.setText(table1.getValueAt(row, 4).toString());
                    tfPassword.setText(table1.getValueAt(row, 5).toString());
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
                dashboardForm.updateUserCount(); // Update user count in the dashboard
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
                dashboardForm.updateUserCount(); // Update user count in the dashboard
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
                dashboardForm.updateUserCount(); // Update user count in the dashboard
            }
        });

    }

    public JPanel getPanel1() {
        return panel1;
    }

    void table_load() {
        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM Users");
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
            table1.setModel(tableModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while loading the table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addUser() {
        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }

            String sql = "INSERT INTO Users (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tfName.getText());
            pst.setString(2, tfEmail.getText());
            pst.setString(3, tfPhone.getText());
            pst.setString(4, tfAddress.getText());
            pst.setString(5, tfPassword.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "User Added Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while adding the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateUser() {
        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
            int selectedRow = table1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a user to update");
                return;
            }
            String selectedUserId = table1.getValueAt(selectedRow, 0).toString();
            String sql = "UPDATE Users SET name = ?, email = ?, phone = ?, address = ?, password = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tfName.getText());
            pst.setString(2, tfEmail.getText());
            pst.setString(3, tfPhone.getText());
            pst.setString(4, tfAddress.getText());
            pst.setString(5, tfPassword.getText());
            pst.setString(6, selectedUserId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "User Updated Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while updating the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
                final String USERNAME = "root";
                final String PASSWORD = "";
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
            int selectedRow = table1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a user to delete");
                return;
            }
            String userId = table1.getValueAt(selectedRow, 0).toString();
            String sql = "DELETE FROM Users WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "User Deleted Successfully");
            table_load(); // Refresh the table
            clearFields(); // Clear text fields
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tfName.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfAddress.setText("");
        tfPassword.setText("");
    }
}
