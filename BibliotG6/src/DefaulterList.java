import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class DefaulterList extends JFrame {
    private JPanel DefaulterListPanel;
    private JLabel btnBack;
    private JTable table1;
    private DashboardForm dashboardForm; // Référence à DashboardForm
    private Connection conn;

    public DefaulterList(DashboardForm dashboardForm) {
        this.dashboardForm = dashboardForm; // Initialisation de la référence
        setTitle("Defaulter List");
        setContentPane(DefaulterListPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                dashboardForm.setVisible(true); // Show the DashboardForm
            }
        });

        connectToDatabase();
        loadDefaultersTable();
    }

    private void loadDefaultersTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("BookId");
        model.addColumn("BookName");
        model.addColumn("UserId");
        model.addColumn("UserName");
        model.addColumn("IssueDate");
        model.addColumn("DueDate");
        model.addColumn("Status");

        try {
            String query = "SELECT BookId, BookName, userId, UserName, issueDate, dueDate, status " +
                    "FROM IssueBooksDetails " +
                    "WHERE Status = 'Pending' AND DueDate < CURRENT_DATE()";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[7];
                for (int i = 0; i < 7; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }

            table1.setModel(model);
            dashboardForm.updateDefaulterListCount(); // Mise à jour du tableau de bord

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/librarymanagementsystem";
        String username = "root";
        String password = "";

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public JPanel getDefaulterListPanel() {
        return DefaulterListPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardForm dashboardForm = new DashboardForm(new Admins()); // Créez un DashboardForm
            DefaulterList defaulterList = new DefaulterList(dashboardForm); // Passez la référence du DashboardForm
            defaulterList.setVisible(true);
        });
    }
}
