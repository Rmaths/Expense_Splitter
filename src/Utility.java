import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Utility {
    public static int getUserId(String username) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query="select User_ID from User where Username=?";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setString(1,username);
        ResultSet resultSet=ps.executeQuery();
        int userid=0;
        if (resultSet.next()){
            userid=resultSet.getInt(1);
        }
        return userid;
    }

    public static String getUserName(int userId) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query="select Username from User where User_ID=?";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setInt(1,userId);
        ResultSet resultSet=ps.executeQuery();
        String userName="";
        if (resultSet.next()){
            userName=resultSet.getString(1);
        }
        return userName;
    }

    public static List<String> getUserGroups(int userid) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query = "SELECT Group_ID, GroupName FROM Groups where Group_ID in (select distinct Group_ID from GroupMember where User_ID=?)"; // Replace column_name and table_name
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1,userid);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<String> groups= new ArrayList<>();

        while(resultSet.next()){
            String groupName= resultSet.getString("GroupName");
            groups.add(groupName);
        }
        System.out.println("Number of available groups for user " + Utility.getUserName(userid) + ": " + groups.size());
        return groups;
    }

    public static int getGroupId(String groupName) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query="select Group_ID from Groups where GroupName=?";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setString(1,groupName);
        ResultSet resultSet=ps.executeQuery();
        int groupid=0;
        if (resultSet.next()){
            groupid=resultSet.getInt(1);
        }
        return groupid;
    }

    public static String getGroupName(int groupId) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query="select GroupName from Groups where Group_ID=?";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setInt(1,groupId);
        ResultSet resultSet=ps.executeQuery();
        String groupid="";
        if (resultSet.next()){
            groupid=resultSet.getString(1);
        }
        return groupid;
    }

    public static List<Integer> getUsersOfGroup(int groupid) throws SQLException {
        Connection connection = MySQLConnection.getConnection();
        String query="SELECT distinct User_ID FROM expense_splitter.groupmember where Group_ID=?;";
        PreparedStatement ps= connection.prepareStatement(query);
        ps.setInt(1,groupid);
        ResultSet resultSet=ps.executeQuery();
        List<Integer> users= new ArrayList<>();
        while (resultSet.next()){
            int user=resultSet.getInt(1);
            users.add(user);
        }
        return users;
    }

}
