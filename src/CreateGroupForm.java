import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class CreateGroupForm  {

    public static JPanel createGroupFormPage(int userid, JFrame dashboardFrame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));

        JPanel usersPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        usersPanel.setBorder(BorderFactory.createTitledBorder("Select Users"));
        usersPanel.setBackground(new Color(255, 255, 255));

        // Group Name Panel
        JPanel groupNamePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        groupNamePanel.setBackground(new Color(240, 240, 240));
        JLabel groupNameLabel = new JLabel("Group Name:");
        JTextField groupNameField;
        groupNameField = new JTextField();

        groupNamePanel.add(groupNameLabel);
        groupNamePanel.add(groupNameField);
        panel.add(groupNamePanel, BorderLayout.CENTER);

        // Users Panel
        ArrayList<JCheckBox> userCheckboxes = new ArrayList<>();
        populateUsers(userCheckboxes, usersPanel, panel);

        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        panel.add(scrollPane, BorderLayout.WEST);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        JButton createGroupButton = new JButton("Create Group");
        JButton cancelButton = new JButton("Cancel");

        buttonsPanel.add(createGroupButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        // Button Listeners
        createGroupButton.addActionListener(e -> {
            try {
                createGroup(groupNameField, userCheckboxes, panel, userid, dashboardFrame);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cancelButton.addActionListener(e -> clearForm(groupNameField, userCheckboxes));
        return panel;
    }

    private static void populateUsers(ArrayList<JCheckBox> userCheckboxes, JPanel usersPanel, JPanel panel) {
        try {
            Connection conn = MySQLConnection.getConnection();
            String query = "SELECT User_id, name FROM User";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String userName = rs.getString("name");

                JCheckBox checkBox = new JCheckBox(userName);
                checkBox.putClientProperty("userId", userId);
                userCheckboxes.add(checkBox);
                usersPanel.add(checkBox);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static void createGroup(JTextField groupNameField, ArrayList<JCheckBox> userCheckboxes, JPanel panel, int userId, JFrame dashboardFrame) throws SQLException {
        String groupName = groupNameField.getText().trim();
        System.out.println("Create group function");
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please enter a group name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Integer> selectedUsers = new ArrayList<>();
        for (JCheckBox checkBox : userCheckboxes) {
            if (checkBox.isSelected()) {
                selectedUsers.add((Integer) checkBox.getClientProperty("userId"));
            }
        }

        if (selectedUsers.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please select at least one user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int groupId = saveGroupToDatabase(groupName, panel);
        if (groupId > 0) {
            saveGroupMembersToDatabase(groupId, panel, selectedUsers);
            JOptionPane.showMessageDialog(panel, "Group created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(groupNameField, userCheckboxes);
            dashboardFrame.dispose();
            Dashboard.displayDashboard(userId);
        }
    }

    private static int saveGroupToDatabase(String groupName, JPanel panel) {
        Connection conn;
        PreparedStatement stmt;
        try {
            conn=MySQLConnection.getConnection();
            String query = "INSERT INTO Groups (groupname) VALUES (?)";
            stmt  = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, groupName);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated group_id
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Indicate failure
    }

    private static void saveGroupMembersToDatabase(int groupId, JPanel panel, ArrayList<Integer> userIds) {
        Connection conn;
        PreparedStatement stmt;
        String query = "INSERT INTO GroupMember (group_id, user_id, sharepercentage) VALUES (?, ?, ?)";
        try {
            conn=MySQLConnection.getConnection();
            stmt = conn.prepareStatement(query);

            for (int userId : userIds) {
                stmt.setInt(1, groupId);
                stmt.setInt(2, userId);
                stmt.setDouble(3, 100.0 / userIds.size()); // Equal share for all users
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearForm(JTextField groupNameField, ArrayList<JCheckBox> userCheckboxes) {
        groupNameField.setText("");
        for (JCheckBox checkBox : userCheckboxes) {
            checkBox.setSelected(false);
        }
    }
}
