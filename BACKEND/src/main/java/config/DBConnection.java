package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("CRITICAL: application.properties not found!");
                URL = System.getenv("DB_URL");
                USER = System.getenv("DB_USERNAME");
                PASSWORD = System.getenv("DB_PASSWORD");
            } else {
                props.load(input);
                URL = resolveProperty(props, "db.url", null);
                USER = resolveProperty(props, "db.username", "root");
                PASSWORD = resolveProperty(props, "db.password", "");
            }
            System.out.println("Database Configuration Initialized.");
        } catch (Exception ex) {
            System.err.println("Failed to load database configuration!");
            ex.printStackTrace();
        }
    }

    private static String resolveProperty(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        
        if (value.startsWith("${") && value.endsWith("}")) {
            String content = value.substring(2, value.length() - 1);
            int colonIndex = content.indexOf(':');
            String envVar = colonIndex != -1 ? content.substring(0, colonIndex) : content;
            String envValue = System.getenv(envVar);
            
            if (envValue != null && !envValue.isEmpty()) return envValue;
            return colonIndex != -1 ? content.substring(colonIndex + 1) : defaultValue;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load MySQL Driver for Render environment
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found in classpath!");
            throw new SQLException("JDBC Driver Missing", e);
        } catch (SQLException e) {
            System.err.println("--- DATABASE CONNECTION FAILURE ---");
            System.err.println("Target URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            throw e;
        }
    }

    public static boolean checkConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}