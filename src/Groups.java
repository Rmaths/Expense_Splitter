import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Groups {

    public static JPanel getGroups(int userid) {
        try {
            // Add labels to the panel for each value
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(240, 240, 240));

            JLabel header = new JLabel("Groups List");
            header.setFont(new Font("Arial", Font.BOLD, 18));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing
            panel.add(header);

            //group list
            List<String> groups=Utility.getUserGroups(userid);
            for (String groupName : groups) {
                int groupId= Utility.getGroupId(groupName);
                JPanel groupPanel = new JPanel(new BorderLayout());
                groupPanel.setMaximumSize(new Dimension(600, 40));
                groupPanel.setBackground(new Color(220, 220, 220));
                groupPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel groupLabel = new JLabel(groupName);
                groupLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JButton openButton = new JButton("Open");
                openButton.setFont(new Font("Arial", Font.PLAIN, 12));
                openButton.setBackground(new Color(0, 123, 255));
                openButton.setForeground(Color.WHITE);
                openButton.setFocusPainted(false);

                groupPanel.add(groupLabel, BorderLayout.WEST);
                groupPanel.add(openButton, BorderLayout.EAST);

                panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
                panel.add(groupPanel);

                openButton.addActionListener(e -> {
                    try {
                        GroupExpense.displayGroupExpensePage(userid, groupId);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
            return panel;
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}