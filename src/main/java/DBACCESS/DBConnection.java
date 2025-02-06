package DBACCESS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//reference: https://www.linkedin.com/learning/java-ee-concurrency-and-multithrea

public class DBConnection {
	public static Connection getConnection() {
		String dbUrl = System.getenv("DB_URL");
		String dbUser = System.getenv("DB_USER");
		String dbPassword = System.getenv("DB_PASSWORD");
		String dbClass = System.getenv("DB_CLASS");

		System.out.println("DB_URL: " + dbUrl);
		System.out.println("DB_USER: " + dbUser);
		System.out.println("DB_CLASS: " + dbClass);

		Connection connection = null;

		try {
			Class.forName(dbClass);
		} catch (ClassNotFoundException e) {
			System.err.println("❌ JDBC Driver not found: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		try {
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		} catch (SQLException e) {
			System.err.println("❌ Database Connection Failed!");
			System.err.println("Error Message: " + e.getMessage());
			e.printStackTrace();
		}

		if (connection != null) {
			System.out.println("✅ Database connection established successfully!");
		} else {
			System.err.println("❌ Connection is NULL!");
		}

		return connection;
	}
}
