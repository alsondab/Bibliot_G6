import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifyUsers {
    private JPanel PanelMain;
    private JLabel btnBack;
    private JButton notifyUserByEmailButton;
    private JTextField tfIssueUserId;
    private JTextField tfIssueBookId;
    private JButton btnFindBook;
    private JLabel tfBN;
    private JLabel tfUN;
    private JLabel tfISD;
    private JLabel tfDD;
    private JLabel tfEM;
    private JFrame frame;
    private DashboardForm dashboardForm;
    private Connection conn;

    // Constructor
    public NotifyUsers(JFrame frame, DashboardForm dashboardForm, Connection conn) {
        this.frame = frame;
        this.dashboardForm = dashboardForm;
        this.conn = conn;

        // Initialize the GUI components
        initializeComponents();
        btnFindBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findBookAndUserDetails();
            }
        });
        notifyUserByEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailNotification();
            }
        });
    }

    private void findBookAndUserDetails() {
        String bookId = tfIssueBookId.getText().trim();
        String userId = tfIssueUserId.getText().trim();

        if (bookId.isEmpty() || userId.isEmpty()) {
            JOptionPane.showMessageDialog(PanelMain, "Please enter both Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT ib.BookId, bd.BookName, ib.UserId, u.name AS UserName,u.email AS UserEmail, ib.issueDate, ib.dueDate " +
                "FROM IssueBooksDetails ib " +
                "JOIN bookdetails bd ON ib.BookId = bd.BookId " +
                "JOIN Users u ON ib.UserId = u.id " +
                "WHERE ib.BookId = ? AND ib.UserId = ?";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, bookId);
            pst.setString(2, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tfEM.setText(rs.getString("UserEmail"));
                tfBN.setText(rs.getString("BookName"));
                tfUN.setText(rs.getString("UserName"));
                tfISD.setText(rs.getString("issueDate"));
                tfDD.setText(rs.getString("dueDate"));
            } else {
                JOptionPane.showMessageDialog(PanelMain, "No record found for the given Book ID and User ID", "Error", JOptionPane.ERROR_MESSAGE);
                // Clear text fields
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(PanelMain, "Failed to fetch data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void emailNotification() {
        String email = tfEM.getText();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "The email address is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subject = "BIBLIOTG6: Reminder of Overdue Book Return";
        String message = "Dear " + tfUN.getText() + ",\n\n" +
                "We hope this message finds you well.\n\n" +
                "This is to remind you that you currently have a book checked out from our library, titled:\n" +
                "\"" + tfBN.getText() + "\"\n\n" +
                "The due date for returning this book was: " + tfDD.getText() + "\n" +
                "Today's date: " + getCurrentDate() + "\n\n" +
                "It appears that the due date has passed, and we kindly request that you return the book at your earliest convenience.\n" +
                "If you have already returned the book, please disregard this message.\n\n" +
                "Thank you for your attention to this matter.\n\n" +
                "Best regards,\n" +
                "BIBLIOTG6";

        try {
            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString()).replace("+", "%20");
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                    .replace("%0A", "%0D%0A"); // For newline

            String uriStr = "mailto:" + email + "?subject=" + encodedSubject + "&body=" + encodedMessage;
            Desktop.getDesktop().mail(new URI(uriStr));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private void initializeComponents() {
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setContentPane(dashboardForm.getPanelMain());
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    public JPanel getPanelMain() {
        return PanelMain;
    }

    public void setVisible(boolean b) {
        frame.setContentPane(PanelMain);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(b);
    }

    private void clearFields() {
        tfEM.setText("");
        tfBN.setText("");
        tfUN.setText("");
        tfISD.setText("");
        tfDD.setText("");
        tfIssueBookId.setText("");
        tfIssueUserId.setText("");
    }
}
