import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnBooks {
    private JLabel btnBack;
    private JLabel tfIssueBk;
    private JLabel tfIssueBN;
    private JLabel tfIssueUI;
    private JLabel tfIssueDate;
    private JTextField tfIssueUserId;
    private JTextField tfIssueBookId;
    private JButton btnFindBook;
    private JLabel tfIssueDue;
    private JButton btnReturnBook;
    private JPanel returnBooksPanel;
    private JLabel tfIssueUN;
    private DashboardForm dashboardForm; // Reference to the DashboardForm
    private Connection conn; // Database connection

    public ReturnBooks(DashboardForm dashboardForm) {
        this.dashboardForm = dashboardForm; // Initialize the reference
        this.conn = dashboardForm.getConnection(); // Get the database connection from the DashboardForm

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(returnBooksPanel);
                parentFrame.dispose(); // Close the ReturnBooks frame

                dashboardForm.setVisible(true); // Show the DashboardForm
            }
        });

        btnFindBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findBookAndUserDetails();
            }
        });

        btnReturnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
    }

    private void findBookAndUserDetails() {
        String bookId = tfIssueBookId.getText().trim();
        String userId = tfIssueUserId.getText().trim();

        if (bookId.isEmpty() || userId.isEmpty()) {
            JOptionPane.showMessageDialog(returnBooksPanel, "Please enter both Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT ib.BookId, bd.BookName, ib.UserId, u.name AS UserName, ib.issueDate, ib.dueDate " +
                "FROM IssueBooksDetails ib " +
                "JOIN bookdetails bd ON ib.BookId = bd.BookId " +
                "JOIN Users u ON ib.UserId = u.id " +
                "WHERE ib.BookId = ? AND ib.UserId = ?";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, bookId);
            pst.setString(2, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tfIssueBk.setText(rs.getString("BookId"));
                tfIssueBN.setText(rs.getString("BookName"));
                tfIssueUI.setText(rs.getString("UserId"));
                tfIssueUN.setText(rs.getString("UserName"));
                tfIssueDate.setText(rs.getString("issueDate"));
                tfIssueDue.setText(rs.getString("dueDate"));
            } else {
                JOptionPane.showMessageDialog(returnBooksPanel, "No record found for the given Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
                // Clear text fields
                clearFields();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(returnBooksPanel, "Failed to fetch data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void returnBook() {
        String bookId = tfIssueBookId.getText().trim();
        String userId = tfIssueUserId.getText().trim();

        if (bookId.isEmpty() || userId.isEmpty()) {
            JOptionPane.showMessageDialog(returnBooksPanel, "Please enter both Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updateQuery = "UPDATE IssueBooksDetails SET status = 'return' WHERE BookId = ? AND UserId = ? AND status = 'pending'";
        String updateBookCountQuery = "UPDATE bookdetails SET Quantity = Quantity + 1 WHERE BookId = ?";

        try (PreparedStatement updatePst = conn.prepareStatement(updateQuery);
             PreparedStatement updateBookCountPst = conn.prepareStatement(updateBookCountQuery)) {

            // Update issue status to 'return'
            updatePst.setString(1, bookId);
            updatePst.setString(2, userId);
            int rowsUpdated = updatePst.executeUpdate();

            if (rowsUpdated > 0) {
                // Update available count of the book
                updateBookCountPst.setString(1, bookId);
                updateBookCountPst.executeUpdate();

                JOptionPane.showMessageDialog(returnBooksPanel, "Book returned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear text fields
                clearFields();
            } else {
                JOptionPane.showMessageDialog(returnBooksPanel, "No pending issue found for the given Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(returnBooksPanel, "Failed to return book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        tfIssueBk.setText("");
        tfIssueBN.setText("");
        tfIssueUI.setText("");
        tfIssueUN.setText("");
        tfIssueDate.setText("");
        tfIssueDue.setText("");
        tfIssueBookId.setText("");
        tfIssueUserId.setText("");
    }

    public JPanel getReturnBooksPanel() {
        return returnBooksPanel;
    }
}
