package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.passwordUtils;

/**
 * Servlet implementation class handleRegisterUser
 */
public class handleRegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Environment variables for database credentials
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	private static final String DB_CLASS = System.getenv("DB_CLASS");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public handleRegisterUser() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private boolean isUsernameExists(String username) {
		System.out.println("Checking username: " + username);
		String checkQuery = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(checkQuery)) {

			stmt.setString(1, username);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0; // Username exists
				} else {
					return false; // No results returned, username does not exist
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false; // Username does not exist
	}

	private boolean registerUser(String username, String password) {
		String insertQuery = "INSERT INTO users (username, password, is_admin, is_blocked) VALUES (?, ?, false, false)";

		// Load the database driver
		try {
			Class.forName(DB_CLASS);
		} catch (ClassNotFoundException e) {
			System.err.println("Database driver not found: " + DB_CLASS);
			e.printStackTrace();
			return false;
		}

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

			// Set query parameters
			stmt.setString(1, username);
			stmt.setString(2, password); // Consider hashing the password for security

			// Execute the query
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0; // Return true if at least one row is inserted

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve form data
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password");

		// Input validation
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			request.setAttribute("error", "All fields are required!");
			request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
			return;
		}

		// Check for duplicate username
		if (isUsernameExists(username)) {
			request.setAttribute("error", "Username already exists!");
			request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
			return;
		}
		String hashedPassword = passwordUtils.hashPassword(password);
		// Save user to the database
		boolean isRegistered = registerUser(username, hashedPassword);

		if (isRegistered) {
			// Redirect to login.jsp after successful registration
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
		} else {
			// Show error message on registration failure
			request.setAttribute("error", "Registration failed. Try again.");
			request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
		}
	}
}
