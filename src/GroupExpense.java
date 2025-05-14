import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupExpense {

    public static void displayGroupExpensePage( int userId, int groupId) throws SQLException {
        JFrame frame = new JFrame("Group Expenses");
        frame.setSize(450, 400);
        System.out.println("User: " + userId);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));

        // Header
        JLabel headerLabel = new JLabel(Utility.getGroupName(groupId), SwingConstants.CENTER);
        headerLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(headerLabel, BorderLayout.NORTH);

        List<Integer> users= Utility.getUsersOfGroup(groupId);
        System.out.println("Number of users in group " + Utility.getGroupName(groupId) + " are: " + users.size());

        //remove self
        //users.remove((Integer) userId);
        for(int user: users) {
            System.out.println("For user: " + Utility.getUserName(user));
            Connection conn = MySQLConnection.getConnection();
            String userQuery = "Select sum(AmountOwed) from expensesplit where User_ID=? and Expense_ID in\n" +
                    " (Select distinct expense_id from all_expense where Group_ID=?)";
            PreparedStatement ps = conn.prepareStatement(userQuery);
            ps.setInt(1,user);
            ps.setInt(2, groupId);
            ResultSet rs = ps.executeQuery();
            int amountOwned = 0;
            if(rs.next()) {
                amountOwned = amountOwned + rs.getInt(1);
            }

            System.out.println("Total amount owned by user " + Utility.getUserName(user) + " is: " + amountOwned);

            JPanel amountOwnedPanel = new JPanel(new BorderLayout());
            amountOwnedPanel.setMaximumSize(new Dimension(600, 40));
            amountOwnedPanel.setBackground(new Color(220, 220, 220));
            amountOwnedPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel amountOwnedUsernameLabel = new JLabel(Utility.getUserName(user) + " owes you");
            amountOwnedUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            JLabel amountOwnedLabel = new JLabel(String.valueOf(amountOwned));
            amountOwnedLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            amountOwnedPanel.add(amountOwnedUsernameLabel,BorderLayout.WEST);
            amountOwnedPanel.add(amountOwnedLabel, BorderLayout.EAST);

            panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
            panel.add(amountOwnedPanel);
            conn.close();
            ps.close();
        }

        Connection connection= MySQLConnection.getConnection();
        String query="select * from all_expense where Group_ID=? order by date desc";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setInt(1,groupId);
        ResultSet resultSet=ps.executeQuery();

        List<ExpenseModel> expenses= new ArrayList<>();
        while(resultSet.next()){

            int userid= resultSet.getInt("User_ID");
            int group_id= resultSet.getInt("Group_ID");
            int expense_id= resultSet.getInt("expense_ID");
            String desc= resultSet.getString("Description");
            double amount= resultSet.getDouble("Amount");
            String date= resultSet.getString("Date");
            ExpenseModel em= new ExpenseModel(desc,date,amount,userid,group_id,expense_id);
            expenses.add(em);

        }
        connection.close();
        ps.close();
        System.out.println(expenses.size());

        JLabel headerGroupLabel = new JLabel("Expenses in group", SwingConstants.CENTER);
        headerGroupLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        headerGroupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(headerGroupLabel, BorderLayout.NORTH);
        for (ExpenseModel exp: expenses ){
            JPanel expensePanel = new JPanel(new BorderLayout());
            expensePanel.setMaximumSize(new Dimension(600, 40));
            expensePanel.setBackground(new Color(220, 220, 220));
            expensePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel descLabel = new JLabel(exp.getDesc());
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            String paidBy = " - [Paid by " + Utility.getUserName(exp.getUserid()) + " on " + exp.getDate() + " ]";
            JLabel dateLabel = new JLabel(paidBy);
            dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JLabel amountLabel = new JLabel(String.valueOf(exp.getAmount()));
            amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));


            expensePanel.add(descLabel,BorderLayout.WEST);
            expensePanel.add(dateLabel,BorderLayout.CENTER);
            expensePanel.add(amountLabel, BorderLayout.EAST);

            panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
            panel.add(expensePanel);
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}
