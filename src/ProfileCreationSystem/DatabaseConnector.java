package ProfileCreationSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// database connection  using the Singleton pattern.
public class DatabaseConnector implements IDBConnection {

    private static DatabaseConnector instance;
    private Connection connection;

    // Private constructor prevents direct object creation
    private DatabaseConnector() {
    }

    // Returns the single shared instance
    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    @Override
    public void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/legends_db",
                        "root",
                        "1234"
                );
                System.out.println("Database connected successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
