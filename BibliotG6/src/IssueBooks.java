import rojeru_san.componentes.RSDateChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class IssueBooks {
    private JPanel PanelMain;
    private JLabel btnBack;
    private JButton btnIssueBook;
    private JTextField tfIssueBookId;
    private JTextField tfIssueUserId;
    private RSDateChooser IssueDate;
    private RSDateChooser IssueDueDate;
    private JLabel tfIssueUid;
    private JLabel tfIssueUN;
    private JLabel tfIssueEm;
    private JLabel tfIssueBk;
    private JLabel tfIssueBN;
    private JLabel tfIssueAtN;
    private JLabel tfIssueQt;
    private JLabel tfIssuePh;
    private JLabel tfIssuePssW;
    private JLabel tfIssueAdr;
    private JFrame issueBooksFrame;
    private DashboardForm dashboardForm;

    public IssueBooks(JFrame issueBooksFrame, DashboardForm dashboardForm) {
        this.issueBooksFrame = issueBooksFrame;
        this.dashboardForm = dashboardForm;

        // Back button listener
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                issueBooksFrame.dispose();  // Close IssueBooks frame
                dashboardForm.setVisible(true);  // Show DashboardForm
            }
        });

        // Add ActionListener to the BookId text field
        tfIssueBookId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchBookDetails();
            }
        });

        // Add ActionListener to the UserId text field
        tfIssueUserId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchUserDetails();
            }
        });

        // Add ActionListener to the Issue Book button
        btnIssueBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertIssueBookDetails();
            }
        });
    }

    // Method to connect to the database
    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/librarymanagementsystem";
        String username = "root";
        String password = "";  // No password
        return DriverManager.getConnection(url, username, password);
    }

    // Method to fetch book details from the database
    private void fetchBookDetails() {
        String bookId = tfIssueBookId.getText();
        System.out.println("Fetching details for BookId: " + bookId);  // Debug message
        String query = "SELECT BookId, BookName, Authors, Quantity FROM bookdetails WHERE BookId = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Book found: " + rs.getString("BookName"));  // Debug message
                tfIssueBk.setText(rs.getString("BookId"));
                tfIssueBN.setText(rs.getString("BookName"));
                tfIssueAtN.setText(rs.getString("Authors"));
                tfIssueQt.setText(rs.getString("Quantity"));
            } else {
                JOptionPane.showMessageDialog(null, "Book not found!");
                clearBookDetails();  // Clear the labels if book not found
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch book details: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }

    // Method to fetch user details from the database
    private void fetchUserDetails() {
        String userId = tfIssueUserId.getText();
        System.out.println("Fetching details for UserId: " + userId);  // Debug message
        String query = "SELECT id, name, email, phone, address, password FROM Users WHERE id = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("User found: " + rs.getString("name"));  // Debug message
                tfIssueUid.setText(rs.getString("id"));
                tfIssueUN.setText(rs.getString("name"));
                tfIssueEm.setText(rs.getString("email"));
                tfIssuePh.setText(rs.getString("phone"));
                tfIssueAdr.setText(rs.getString("address"));
                tfIssuePssW.setText(rs.getString("password"));
            } else {
                JOptionPane.showMessageDialog(null, "User not found!");
                clearUserDetails();  // Clear the labels if user not found
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch user details: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }

    // Method to insert issue book details into the database
    private void insertIssueBookDetails() {
        String bookId = tfIssueBk.getText();
        String userId = tfIssueUid.getText();

        // Check if the user has already borrowed 2 books
        if (countBooksIssuedToUser(userId) >= 2) {
            JOptionPane.showMessageDialog(null, "You have already borrowed 2 books. Return some books before borrowing more.");
            return;
        }

        // Check if the book has already been issued to the same user
        if (isBookAlreadyIssuedToUser(bookId, userId)) {
            JOptionPane.showMessageDialog(null, "This book has already been allocated to this user!");
            return;
        }

        // Check if the book is available
        int availableQuantity = Integer.parseInt(tfIssueQt.getText());
        if (availableQuantity <= 0) {
            JOptionPane.showMessageDialog(null, "This book is not available at the moment!");
            return;
        }

        // Check if the Issue Date and Due Date are selected
        if (IssueDate.getDatoFecha() == null || IssueDueDate.getDatoFecha() == null) {
            JOptionPane.showMessageDialog(null, "Please choose both Issue Date and Due Date.");
            return;
        }

        // Calculate the due date
        long issueDateTime = IssueDate.getDatoFecha().getTime();
        long dueDateTime = IssueDueDate.getDatoFecha().getTime();
        long twoWeeksInMillis = 2 * 7 * 24 * 60 * 60 * 1000; // 2 weeks in milliseconds
        if (dueDateTime - issueDateTime > twoWeeksInMillis) {
            JOptionPane.showMessageDialog(null, "You can't issue a book for more than 2 weeks!");
            return;
        }

        // Proceed with issuing the book
        String bookName = tfIssueBN.getText();
        Date issueDate = new Date(issueDateTime);
        Date dueDate = new Date(dueDateTime);
        String status = "Pending";  // Default status

        String query = "INSERT INTO IssueBooksDetails (BookId, BookName, UserId, issueDate, dueDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.setString(2, bookName);
            stmt.setString(3, userId);

            stmt.setDate(4, issueDate);
            stmt.setDate(5, dueDate);
            stmt.setString(6, status);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Book issued successfully!");
                // Update quantity in the bookdetails table
                updateBookQuantity(bookId, availableQuantity - 1);
                // Clear the book details fields
                clearBookDetails();
                // Clear the user details fields
                clearUserDetails();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to issue book!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to insert issue book details: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }

    // Method to update the quantity of the book in the bookdetails table
    private void updateBookQuantity(String bookId, int quantity) {
        String query = "UPDATE bookdetails SET Quantity = ? WHERE BookId = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, bookId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Quantity updated successfully.");
            } else {
                System.out.println("Failed to update quantity.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to update book quantity: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }

    // Method to check if the book has already been issued to the same user
    private boolean isBookAlreadyIssuedToUser(String bookId, String userId) {
        String query = "SELECT COUNT(*) FROM IssueBooksDetails WHERE BookId = ? AND UserId = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to check if book is already issued to user: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
        return false;
    }

    // Method to count the number of books issued to a user
    private int countBooksIssuedToUser(String userId) {
        String query = "SELECT COUNT(*) FROM IssueBooksDetails WHERE UserId = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to count books issued to user: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
        return 0;
    }

    // Method to clear the book details labels
    private void clearBookDetails() {
        tfIssueBk.setText("");
        tfIssueBN.setText("");
        tfIssueAtN.setText("");
        tfIssueQt.setText("");
    }

    // Method to clear the user details labels
    private void clearUserDetails() {
        tfIssueUid.setText("");
        tfIssueUN.setText("");
        tfIssueEm.setText("");
        tfIssuePh.setText("");
        tfIssueAdr.setText("");
        tfIssuePssW.setText("");
    }

    public JPanel getPanelMain() {
        return PanelMain;
    }
}
