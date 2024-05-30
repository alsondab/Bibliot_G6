import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SignupPage extends JDialog {
    private JPanel signupPanel;
    private JTextField tfName;
    private JTextField tfEmail;
    private JPasswordField tfConfirmPassword;
    private JPasswordField tfPassword;
    private JTextField tfAddress;
    private JTextField tfPhone;
    private JButton btnRegister;
    private JButton btnCancel;

    public SignupPage(JFrame parent) {
        super(parent);
        setTitle("Create a New Account");
        setContentPane(signupPanel);
        setMinimumSize(new Dimension(600, 674));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signupAdmins();
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

    private void signupAdmins() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(tfPassword.getPassword());
        String confirmPassword = String.valueOf(tfConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please all Field are required",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Admins admins = addAdminstoDatabase(name, email, phone, address, password);
        if (admins != null) {
            this.admins = admins;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new admins",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Admins admins;

    private Admins addAdminstoDatabase(String name, String email, String phone, String address, String password) {
        Admins admins = null;

        final String DB_URL = "jdbc:mysql://localhost/librarymanagementsystem?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to database successfully

            // Check if the email already exists
            String checkQuery = "SELECT COUNT(*) FROM admins WHERE email = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setString(1, email);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                // Email already exists, handle accordingly (e.g., display error message)
                JOptionPane.showMessageDialog(this,
                        "Email address already exists",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Insert the new admins
            String insertQuery = "INSERT INTO admins (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            // Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                admins = new Admins();
                admins.name = name;
                admins.email = email;
                admins.phone = phone;
                admins.address = address;
                admins.password = password;
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return admins;
    }


    public static void main(String[] args) {
        SignupPage myForm = new SignupPage(null);
        Admins admins = myForm.admins;
        if (admins != null) {
            JOptionPane.showMessageDialog(null, "Successful Registration of: " + admins.name, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Registration Canceled", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

