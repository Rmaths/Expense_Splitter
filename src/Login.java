import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    public static boolean authenticate(String username, String password) {
        boolean isAuthenticated = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = MySQLConnection.getConnection();

            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                isAuthenticated = true;
            }

        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) MySQLConnection.closeConnection(connection);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return isAuthenticated;
    }

    public static void displayLoginPage() {
        JFrame frame = new JFrame("Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // Create labels and text fields
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(60, 63, 65));

        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(50, 50, 100, 30);
        labelUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        labelUsername.setForeground(Color.WHITE);
        panel.add(labelUsername);

        JTextField textUsername = new JTextField();
        textUsername.setBounds(150, 50, 180, 30);
        textUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textUsername);

        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(50, 120, 100, 30);
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        labelPassword.setForeground(Color.WHITE);
        panel.add(labelPassword);

        JPasswordField textPassword = new JPasswordField();
        textPassword.setBounds(150, 120, 180, 30);
        textPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textPassword);

        // Create login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(80, 180, 100, 30);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        panel.add(loginButton);

        // Create registration button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(200, 180, 100, 30);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        panel.add(registerButton);

        // Add action listener to the login button
        loginButton.addActionListener(e -> {
            String username = textUsername.getText();
            String password = new String(textPassword.getPassword());

            // Call authentication method
            if (Login.authenticate(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();

                try {
                    int userid= Utility.getUserId(username);
                    Dashboard.displayDashboard(userid);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.getRootPane().setDefaultButton(loginButton);
        registerButton.addActionListener(e -> {
            frame.dispose();
            UserRegistration.newUserRegistrationPage();
        });

        // Set frame visibility
        frame.add(panel);
        frame.setVisible(true);

        if (authenticate(textUsername.getText(), textPassword.toString())) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}