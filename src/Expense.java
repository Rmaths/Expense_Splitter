import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class Expense {

    public static JPanel getUserExpenseData(int userid, JFrame dashboardFrame) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        JLabel header = new JLabel("Expenses Done by Me ");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing
        panel.add(header);



        try {
            Connection connection = MySQLConnection.getConnection();
            String query = "SELECT Description, Amount, Date FROM all_expense Where User_ID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userid);
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<ExpenseModel> expenses= new ArrayList<>();
            while (resultSet.next()) {
                ExpenseModel em = new ExpenseModel();
                String desc = resultSet.getString("Description");
                String date = resultSet.getString("Date");
                Double amount = resultSet.getDouble("Amount");
                em.setDesc(desc);
                em.setDate(date);
                em.setAmount(amount);
                expenses.add(em);
            }

            for(ExpenseModel exp: expenses){
                JPanel expensePanel = new JPanel(new BorderLayout());
                expensePanel.setMaximumSize(new Dimension(600, 40));
                expensePanel.setBackground(new Color(220, 220, 220));
                expensePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel descLabel = new JLabel(exp.getDesc());
                descLabel.setFont(new Font("Arial", Font.PLAIN, 14));


                JLabel dateLabel = new JLabel(" ["+exp.getDate()+"]");
                dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JLabel amountLabel = new JLabel(String.valueOf(exp.getAmount()));
                amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));


                expensePanel.add(descLabel,BorderLayout.WEST);
                expensePanel.add(dateLabel,BorderLayout.CENTER);
                expensePanel.add(amountLabel, BorderLayout.EAST);


                panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
                panel.add(expensePanel);

            }
            JButton addExpenseButton = new JButton("Add Expense");
            addExpenseButton.setFont(new Font("Arial", Font.PLAIN, 12));
            addExpenseButton.setBackground(new Color(0, 123, 255));
            addExpenseButton.setForeground(Color.WHITE);
            addExpenseButton.setFocusPainted(false);
            addExpenseButton.addActionListener(e -> {
                try {
                    AddExpense.addExpenseFrom(userid, dashboardFrame);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                Dashboard.createAllExpensePanel(userid, dashboardFrame);
            });


            panel.add(addExpenseButton, BorderLayout.AFTER_LAST_LINE);
            // Close resources
            resultSet.close();
            preparedStatement.close();
            MySQLConnection.closeConnection(connection);
            return panel;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

