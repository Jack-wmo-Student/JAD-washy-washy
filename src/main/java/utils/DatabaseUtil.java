package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import MODEL.DAO.*;
public class DatabaseUtil {
    // Environment variable names
    private static final String ENV_DB_URL = "DB_URL";
    private static final String ENV_DB_USER = "DB_USER";
    private static final String ENV_DB_PASSWORD = "DB_PASSWORD";
    private static final String ENV_DB_CLASS = "DB_CLASS";
    
    // Default values (optional, for development)
    private static final String DEFAULT_DB_URL = "DB_URL";
    private static final String DEFAULT_DB_CLASS = "com.mysql.cj.jdbc.Driver";
    
    static {
        try {
            // Load the database driver
            String driverClass = getEnvironmentVariable(ENV_DB_CLASS, DEFAULT_DB_CLASS);
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Failed to load database driver: " + e.getMessage());
        }
    }
    
    /**
     * Gets a database connection using environment variables.
     * 
     * @return A connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        String dbUrl = getRequiredEnvironmentVariable(ENV_DB_URL);
        String dbUser = getRequiredEnvironmentVariable(ENV_DB_USER);
        String dbPassword = getRequiredEnvironmentVariable(ENV_DB_PASSWORD);
        
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * Gets a required environment variable, throws an exception if not found.
     * 
     * @param name The name of the environment variable
     * @return The value of the environment variable
     * @throws IllegalStateException if the environment variable is not set
     */
    private static String getRequiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                "Required environment variable '" + name + "' is not set. " +
                "Please configure your environment variables properly."
            );
        }
        return value;
    }
    
    /**
     * Gets an environment variable with a default fallback value.
     * 
     * @param name The name of the environment variable
     * @param defaultValue The default value to use if the environment variable is not set
     * @return The value of the environment variable or the default value
     */
    private static String getEnvironmentVariable(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Checks if all required database environment variables are set.
     * Useful for application startup validation.
     * 
     * @throws IllegalStateException if any required environment variable is missing
     */
    public static void validateEnvironmentVariables() {
        getRequiredEnvironmentVariable(ENV_DB_URL);
        getRequiredEnvironmentVariable(ENV_DB_USER);
        getRequiredEnvironmentVariable(ENV_DB_PASSWORD);
    }
}