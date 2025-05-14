import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class MySQLConnection {

        // Database credentials
        private static final String URL = "jdbc:mysql://localhost:3306/test";
        private static final String USER = "root";
        private static final String PASSWORD = "mysql@123";

        /**
         * Establishes a connection to the MySQL database.
         *
         * @return Connection object if successful, otherwise null
         */
        public static Connection getConnection() {
            Connection connection = null;
            try {
                // Load MySQL JDBC Driver (optional for newer Java versions)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
//                System.out.println("Connection established successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database: " + e.getMessage());
            }

            return connection;
        }

        /**
         * Closes the given database connection.
         *
         * @param connection The connection to close
         */
        public static void closeConnection(Connection connection) {
            if (connection != null) {
                try {
                    connection.close();
//                    System.out.println("Connection closed successfully.");
                } catch (SQLException e) {
                    System.err.println("Failed to close the connection: " + e.getMessage());
                }
            }
        }

        public static void main(String[] args) {
            // Test the connection
            Connection connection = getConnection();

            // Perform your database operations here

            // Close the connection
            closeConnection(connection);
        }
    }
