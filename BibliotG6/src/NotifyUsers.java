import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

public class NotifyUsers {
    private JPanel PanelMain;
    private JLabel btnBack;
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
}
