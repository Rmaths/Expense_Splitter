import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Dashboard {

    public static void displayDashboard(int userid) throws SQLException {
        // Create JFrame
        JFrame frame = new JFrame("Dashboard");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main container panel with BorderLayout
        JPanel container = new JPanel(new BorderLayout());

        // Left-side menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 10, 10));
        menuPanel.setBackground(new Color(43, 45, 47));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        // Buttons for the menu
        JButton displayGroupsButton = new JButton("Display Groups");
        JButton makeGroupsButton = new JButton("Make Groups");
        JButton expensesButton = new JButton("Expenses");

        styleMenuButton(displayGroupsButton);
        styleMenuButton(makeGroupsButton);
        styleMenuButton(expensesButton);

        menuPanel.add(displayGroupsButton);
        menuPanel.add(makeGroupsButton);
        menuPanel.add(expensesButton);

        // Right-side dynamic content panel
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);

        // Panels for each section
        JPanel displayGroupsPanel = createDisplayGroupsPanel(userid);
        JPanel createGroupsPanel = creteGroupPanel(userid, frame);
        createGroupsPanel.setBackground(new Color(240, 240, 240));
        createGroupsPanel.add(new JLabel("Making Groups Section"));

        JPanel expensesPanel = createAllExpensePanel(userid, frame);
        expensesPanel.setBackground(new Color(240, 240, 240));

        // Add these panels to contentPanel
        contentPanel.add(displayGroupsPanel, "DisplayGroups");
        contentPanel.add(createGroupsPanel, "MakeGroups");
        contentPanel.add(expensesPanel, "Expenses");

        // Action Listeners for menu buttons
        displayGroupsButton.addActionListener(e -> switchPanel(contentPanel, "DisplayGroups"));
        makeGroupsButton.addActionListener(e -> switchPanel(contentPanel, "MakeGroups"));
        expensesButton.addActionListener(e -> switchPanel(contentPanel, "Expenses"));

        // Add panels to container
        container.add(menuPanel, BorderLayout.WEST);
        container.add(contentPanel, BorderLayout.CENTER);

        // Add container to frame
        frame.add(container);
        frame.setVisible(true);
    }


    private static void styleMenuButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private static JPanel createDisplayGroupsPanel(int userid) {
       return Groups.getGroups(userid);
    }

    public static JPanel creteGroupPanel(int userid, JFrame frame) {
        return CreateGroupForm.createGroupFormPage(userid, frame);
    }

    public static JPanel createAllExpensePanel(int userid, JFrame frame) {
        return Expense.getUserExpenseData(userid, frame);
    }

    private static void switchPanel(JPanel contentPanel, String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
    }
}
