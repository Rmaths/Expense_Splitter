import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRegistration {

    public static boolean registerUser(String name, String username, String password, String email, String phoneNumber ) {
        boolean isRegistered = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MySQLConnection.getConnection();

            // Prepare SQL query
            String query = "INSERT INTO user (name, username, password, email, phonenumber) VALUES (?, ?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phoneNumber);

            // Execute update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                isRegistered = true;
                System.out.println("User registered successfully.");
            }

        } catch (SQLException e) {
            System.err.println("Error during user registration: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) MySQLConnection.closeConnection(connection);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return isRegistered;
    }

    public static void newUserRegistrationPage() {

        JFrame frame = new JFrame("Registration Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLocationRelativeTo(null);

        // create panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(60, 63, 65));
        panel.setLayout(null);

        // Create labels and text fields
        JLabel titleLabel = new JLabel("Register Now");
        titleLabel.setBounds(140, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JLabel labelName = new JLabel("Name:");
        labelName.setBounds(50, 70, 100, 30);
        labelName.setFont(new Font("Arial", Font.PLAIN, 14));
        labelName.setForeground(Color.WHITE);
        panel.add(labelName);

        JTextField textName = new JTextField();
        textName.setBounds(150, 70, 220, 30);
        textName.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textName);

        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(50, 110, 100, 30);
        labelUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        labelUsername.setForeground(Color.WHITE);
        panel.add(labelUsername);

        JTextField textUsername = new JTextField();
        textUsername.setBounds(150, 110, 220, 30);
        textUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textUsername);

        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(50, 150, 100, 30);
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        labelPassword.setForeground(Color.WHITE);
        panel.add(labelPassword);

        JPasswordField textPassword = new JPasswordField();
        textPassword.setBounds(150, 150, 220, 30);
        textPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textPassword);

        JLabel labelConfirmPassword = new JLabel("Confirm Password:");
        labelConfirmPassword.setBounds(50, 270, 100, 30);
        labelConfirmPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        labelConfirmPassword.setForeground(Color.WHITE);
        panel.add(labelConfirmPassword);

        JPasswordField textConfirmPassword = new JPasswordField();
        textConfirmPassword.setBounds(150, 270, 220, 30);
        textConfirmPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textConfirmPassword);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setBounds(50, 190, 100, 30);
        labelEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        labelEmail.setForeground(Color.WHITE);
        panel.add(labelEmail);

        JTextField textEmail = new JTextField();
        textEmail.setBounds(150, 190, 220, 30);
        textEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textEmail);

        JLabel labelPhone = new JLabel("Phonenumber:");
        labelPhone.setBounds(50, 230, 100, 30);
        labelPhone.setFont(new Font("Arial", Font.PLAIN, 14));
        labelPhone.setForeground(Color.WHITE);
        panel.add(labelPhone);

        JTextField textPhone = new JTextField();
        textPhone.setBounds(150, 230, 220, 30);
        textPhone.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textPhone);



        // Create register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(150, 310, 150, 40);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        panel.add(registerButton);

        // Add action listener to the register button
        registerButton.addActionListener(e -> {
            String Name = textName.getText();
            String Username = textUsername.getText();
            String Password = new String(textPassword.getPassword());
            String  Email= textEmail.getText();
            String Phone= textPhone.getText();
            String confirmPassword = new String(textConfirmPassword.getPassword());



            if (!Password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call registration method
            if (UserRegistration.registerUser(Name, Username, Password, Email, Phone)) {
                JOptionPane.showMessageDialog(frame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                Login.displayLoginPage();
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Set frame visibility
        frame.add(panel);
        frame.setVisible(true);

    }
}
