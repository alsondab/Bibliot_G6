import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JDialog {
    private JPanel loginPanel;
    private JTextField tfEmail;
    private JPasswordField tfPassword;
    private JButton btnOk;
    private JButton btnCancel;
    public Admins admins;

    public LoginPage(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(950, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(tfPassword.getPassword());

                admins = getAuthenticationAdmins(email, password);
                if (admins != null) {
                    dispose();
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Successful Authentication of: " + admins.name + "\n" +
                                    "Email: " + admins.email + "\n" +
                                    "Phone: " + admins.phone + "\n" +
                                    "Address: " + admins.address,
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Email or Password invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private Admins getAuthenticationAdmins(String email, String password) {
        Admins admins = null;
        final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM Admins WHERE email=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                admins = new Admins();
                admins.name = resultSet.getString("name");
                admins.email = resultSet.getString("email");
                admins.phone = resultSet.getString("phone");
                admins.address = resultSet.getString("address");
                admins.password = resultSet.getString("password");
            }

            preparedStatement.close();
            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return admins;
    }

    public static void main(String[] args) {
        LoginPage loginForm = new LoginPage(null);
        Admins admins = loginForm.admins;
        if (admins != null) {
            JOptionPane.showMessageDialog(null, "Successful Authentication of : " + admins.name, "Success", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, "Email: " + admins.email, "Admins Details", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, "Phone: " + admins.phone, "Admins Details", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, "Address: " + admins.address, "Admins Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Authentication Canceled", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}