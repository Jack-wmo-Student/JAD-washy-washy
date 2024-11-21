package backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class landingPage
 */
public class landingPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Environment variables for database credentials
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

	public landingPage() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		User user = validateUser(username, password);

		if (user != null) {
			// Set cookies for session validation
			Cookie isLoggedInCookie = new Cookie("isLoggedIn", "true");
			Cookie isAdminCookie = new Cookie("isAdmin", String.valueOf(user.isAdmin()));
			isLoggedInCookie.setHttpOnly(true);
			isAdminCookie.setHttpOnly(true);

			// Set cookie expiry (1 hour)
			isLoggedInCookie.setMaxAge(60 * 60);
			isAdminCookie.setMaxAge(60 * 60);

			response.addCookie(isLoggedInCookie);
			response.addCookie(isAdminCookie);

			// Redirect to homePage.jsp
			response.sendRedirect(request.getContextPath() + "/categoryService"); // Correct path to homePage.jsp
		} else {
			// Set error message and forward back to index.jsp
			request.setAttribute("error", "Invalid username or password.");
			request.getRequestDispatcher("/pages/index.jsp").forward(request, response); // Correct path to index.jsp
		}
	}

	/**
	 * Validate the user credentials against the database.
	 *
	 * @param username The username entered by the user.
	 * @param password The password entered by the user.
	 * @return A User object if the credentials are valid, null otherwise.
	 */
	private User validateUser(String username, String password) {
		String DB_CLASS = System.getenv("DB_CLASS");
		try {
			Class.forName(DB_CLASS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String query = "SELECT username, password, is_admin FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
		User user = null;

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, username);
			stmt.setString(2, password);

			System.out.println("Executing query: " + query);
			System.out.println("Parameters: Username = " + username + ", Password = " + password);

			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("User found in database: " + rs.getString("username"));
					boolean isAdmin = rs.getBoolean("is_admin");
					user = new User(username, isAdmin);
				} else {
					System.out.println("No matching user found in database.");
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	/**
	 * User model to encapsulate user details.
	 */
	class User {
		private String username;
		private boolean isAdmin;

		public User(String username, boolean isAdmin) {
			this.username = username;
			this.isAdmin = isAdmin;
		}

		public String getUsername() {
			return username;
		}

		public boolean isAdmin() {
			return isAdmin;
		}
	}
}
