package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    // Absolute path to your SQLite DB file
    private static final String URL = "jdbc:sqlite:C:\\InventoryProject\\InventoryProject\\inventory.db";

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Add sqlite-jdbc.jar to classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        enableForeignKeys(conn);
        return conn;
    }

    // Optional: ensure foreign keys are enforced
    private static void enableForeignKeys(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            System.err.println("Failed to enable foreign keys: " + e.getMessage());
        }
    }
}
