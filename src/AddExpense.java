import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class AddExpense {

    public static void addExpenseFrom(int userid, JFrame dashboardFrame) throws SQLException {
        // Create JFrame for Add Expense Form
        JFrame frame = new JFrame("Add Expense");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        frame.add(panel);

        // Header
        JLabel headerLabel = new JLabel("Add New Expense", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        headerLabel.setForeground(new Color(70, 130, 180));
        headerLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(250, 250, 250));

        // Form fields
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JLabel dateLabel = new JLabel("Date (dd/MM/yyyy):");
        JTextField dateField = new JTextField();
        JLabel groupLabel = new JLabel("Group:");
        JComboBox<String> groupDropdown = new JComboBox<>();

        populateGroupDropdown(groupDropdown, userid);

        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(groupLabel);
        formPanel.add(groupDropdown);
        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton addButton = new JButton("Add Expense");
        JButton cancelButton = new JButton("Cancel");

        // Button styles
        addButton.setFont(new Font("Verdana", Font.BOLD, 14));
        addButton.setBackground(new Color(60, 179, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        cancelButton.setFont(new Font("Verdana", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Frame visibility
        frame.setVisible(true);

        // Action listener for Add Expense button
        addButton.addActionListener(e -> {
            String description = descriptionField.getText().trim();
            String amountText = amountField.getText().trim();
            String dateText = dateField.getText().trim();
            String selectedGroup = (String) groupDropdown.getSelectedItem();
            int groupId;
            try {
                groupId = Utility.getGroupId(selectedGroup);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            System.out.println("Date print"+dateText);

            if (description.isEmpty() || amountText.isEmpty() || dateText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);

                if (addExpenseToDatabase(description, amount, dateText , userid, groupId)) {
                    JOptionPane.showMessageDialog(frame, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    dashboardFrame.dispose();
                    Dashboard.displayDashboard(userid);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add expense.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Amount must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Action listener for Cancel button
        cancelButton.addActionListener(e -> frame.dispose());
    }

    private static void populateGroupDropdown(JComboBox<String> groupDropdown, int userid) throws SQLException {
        List<String> groups= Utility.getUserGroups(userid);
        for(String group:groups){
            groupDropdown.addItem(group);
        }
    }

    private static boolean addExpenseToDatabase(String description, double amount, String date, int userid,int groupId)  {
       try{
        Connection conn = MySQLConnection.getConnection();
        String query = "INSERT INTO all_expense (description, amount, date, User_ID, Group_ID) VALUES (?, ?, ?, ?,?) ";

            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, description);
            stmt.setDouble(2, amount);
            stmt.setString(3, date);
            stmt.setInt(4,userid);
            stmt.setInt(5,groupId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated key
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    List<Integer> users = Utility.getUsersOfGroup(groupId);
                    int expenseId = generatedKeys.getInt(1); // Get the first column (auto-generated ID)
                    System.out.println("Generated ID: " + expenseId);
                    addExpenseToSplit(conn, expenseId, users, userid, amount);
                } else {
                   System.out.println("No ID was generated.");
                }
            } else {
                System.out.println("Insert failed.");
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void addExpenseToSplit(Connection conn, int expenseId, List<Integer> users, int userId, double amount) {
        try {
            double individualAmount = amount/users.size();
            users.remove((Integer) userId);
            for(int user: users) {
                String query = "Insert into expensesplit (Expense_Id, User_Id, AmountOwed) values (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, expenseId);
                ps.setInt(2, user);
                ps.setDouble(3, individualAmount);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
