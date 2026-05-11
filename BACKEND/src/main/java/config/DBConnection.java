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
                System.out.println("Sorry, unable to find application.properties");
                // Fallback to defaults if file not found
                URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/atm_db";
                USER = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "root";
                PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "password";
            } else {
                props.load(input);
                // Helper to resolve ${VAR:default} pattern
                URL = resolveProperty(props, "db.url", "jdbc:mysql://localhost:3306/atm_db");
                USER = resolveProperty(props, "db.username", "root");
                PASSWORD = resolveProperty(props, "db.password", "password");
                
                System.out.println("Database Config Loaded: " + URL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String resolveProperty(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        
        // Handle Spring-style placeholders: ${ENV_VAR:default_value}
        if (value.startsWith("${") && value.endsWith("}")) {
            String content = value.substring(2, value.length() - 1);
            int colonIndex = content.indexOf(':');
            String envVar = colonIndex != -1 ? content.substring(0, colonIndex) : content;
            String envValue = System.getenv(envVar);
            
            if (envValue != null) return envValue;
            return colonIndex != -1 ? content.substring(colonIndex + 1) : defaultValue;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("CRITICAL: Failed to connect to database at " + URL);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    public static boolean checkConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                System.out.println("Database connection verified successfully.");
            }
            return isValid;
        } catch (SQLException e) {
            // Error is already logged in getConnection()
            return false;
        }
    }
}