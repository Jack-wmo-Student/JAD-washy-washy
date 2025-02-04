package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.passwordUtils;
import utils.sessionUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet for updating user details directly with embedded database logic.
 */
public class handleUpdateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Environment variables for database credentials
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	private static final String DB_CLASS = System.getenv("DB_CLASS");

	public handleUpdateUser() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Load the database driver
		try {
			Class.forName(DB_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			request.setAttribute("error", "Internal error: Database driver not found.");
			request.getRequestDispatcher("/pages/homePage.jsp").forward(request, response);
			return;
		}

		// Get session and validate authentication
		HttpSession session = request.getSession(false);
		if (session == null) {
			System.out.println("Session is null.");
		} else {
			System.out.println("Session userId: " + session.getAttribute("userId"));
		}

		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null
				|| session.getAttribute("userId") == null) {
			System.out.println("User is not logged in or session is invalid.");
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

		int userId = (int) session.getAttribute("userId");

		// Retrieve form parameters
		String newUsername = request.getParameter("username");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");

		if (!password.equals(confirmPassword)) {
			System.out.println("Passwords do not match");
			request.setAttribute("error", "Passwords do not match.");
			request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
			return;
		}

		String hashedPassword = passwordUtils.hashPassword(password);

		// Validate input
		if (newUsername == null || newUsername.trim().isEmpty()) {
			request.setAttribute("error", "Username cannot be empty.");
			request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
			return;
		}

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			// Check if the username already exists in the database
			if (isUsernameTaken(connection, newUsername, userId)) {
				request.setAttribute("error", "The username is already taken. Please choose another one.");
				request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
				return;
			}

			// Update user details in the database
			if (updateUserDetails(connection, userId, newUsername, hashedPassword)) {
				// Update session attributes
				session.setAttribute("username", newUsername);
				request.setAttribute("success", "Your account details have been successfully updated.");
				request.getRequestDispatcher("/pages/homePage.jsp").forward(request, response);
			} else {
				request.setAttribute("error", "An error occurred while updating your details. Please try again.");
				request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "An internal error occurred. Please try again.");
			request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
		}
	}

	/**
	 * Checks if the given username is already taken by another user.
	 */
	private boolean isUsernameTaken(Connection connection, String username, int userId) throws Exception {
		String query = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setInt(2, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0; // Return true if count > 0
				}
			}
		}
		return false;
	}

	/**
	 * Updates the user details in the database.
	 */
	private boolean updateUserDetails(Connection connection, int userId, String username, String password)
			throws Exception {
		String query;
		if (password != null && !password.trim().isEmpty()) {
			query = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
		} else {
			query = "UPDATE users SET username = ? WHERE user_id = ?";
		}

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			if (password != null && !password.trim().isEmpty()) {
				stmt.setString(2, password);
				stmt.setInt(3, userId);
			} else {
				stmt.setInt(2, userId);
			}
			return stmt.executeUpdate() > 0; // Return true if update was successful
		}
	}
}
