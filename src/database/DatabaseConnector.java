package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // Update these credentials to match your local MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/legends_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private Connection connection;

    public void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        openConnection();
        return connection;
    }
}