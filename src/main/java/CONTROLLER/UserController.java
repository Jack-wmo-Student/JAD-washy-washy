package CONTROLLER;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import MODEL.CLASS.User;
import MODEL.DAO.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.passwordUtils;
import utils.sessionUtils;

public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			System.out.println("UserController doGet method called");

			// Check if user is logged in and is admin
			if (!validateAdminAccess(request, response)) {
				System.out.println("Admin access validation failed");
				return;
			}

			// Get the current user from session
			HttpSession session = request.getSession(false);
			User userObject = null;
			if (session != null) {
				userObject = (User) session.getAttribute("currentUser");
			}

			// Redirect to login if no user found
			if (userObject == null) {
				System.out.println("No user found in session");
				request.setAttribute("error", "Please log in to access this page");
				request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
				return;
			}

			// Get all users and forward to JSP
			List<User> users = userDAO.getAllUsers();
			System.out.println("Retrieved " + users.size() + " users");
			request.setAttribute("users", users);

			RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			System.out.println("Error in UserController: " + e.getMessage());
			e.printStackTrace();
			request.setAttribute("error", "Error retrieving users: " + e.getMessage());
			request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Validate admin access
		if (!validateAdminAccess(request, response)) {
			return;
		}

		try {
			// Get the current user and target user IDs
			HttpSession session = request.getSession(false);
			User currentUser = (User) session.getAttribute("currentUser");
			int targetUserId = Integer.parseInt(request.getParameter("userId"));
			String action = request.getParameter("action");
			// Handle different actions
			String message = null;
			switch (action) {
			case "toggle-block":
				userDAO.toggleUserBlock(targetUserId, currentUser.getUserId());
				message = "User block status updated successfully";
				break;

			case "toggle-admin":
				userDAO.toggleAdminStatus(targetUserId, currentUser.getUserId());
				message = "User admin status updated successfully";
				break;

			default:
				request.setAttribute("error", "Invalid action");
				break;
			}

			// Set success message if action was successful
			if (message != null) {
				request.setAttribute("success", message);
			}

			// Refresh user list and redisplay the page
			List<User> users = userDAO.getAllUsers();
			request.setAttribute("users", users);
			request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);

		} catch (NumberFormatException e) {
			request.setAttribute("error", "Invalid user ID format");
			request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
		} catch (Exception e) {
			request.setAttribute("error", "Error: " + e.getMessage());
			request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
			return;
		}

		int userId = user.getUserId();

		// Read JSON request body
		StringBuilder jsonData = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonData.append(line);
		}
		// Parse JSON manually
		String jsonString = jsonData.toString();
		String username = jsonString.replaceAll(".*\"username\":\"([^\"]+)\".*", "$1");
		String password = jsonString.replaceAll(".*\"password\":\"([^\"]+)\".*", "$1");
		String confirmPassword = jsonString.replaceAll(".*\"confirmPassword\":\"([^\"]+)\".*", "$1");

		// Validate inputs
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()
				|| confirmPassword == null || confirmPassword.trim().isEmpty()) {
			response.sendRedirect(
					request.getContextPath() + "/pages/accountSettings.jsp?error=All fields are required.");
			return;
		}

		// Check if username is already taken
		try {
			if (userDAO.isUsernameTaken(username, userId)) {
				response.sendRedirect(
						request.getContextPath() + "/pages/accountSettings.jsp?error=Username is already taken.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Check if password and confirmPassword match
		if (!password.equals(confirmPassword)) {
			response.sendRedirect(
					request.getContextPath() + "/pages/accountSettings.jsp?error=Passwords do not match.");
			return;
		}

		// Hash the password before saving
		String hashedPassword = passwordUtils.hashPassword(password);

		// Update user details
		int rowsAffected = 0;
		try {
			rowsAffected = userDAO.updateUser(username, hashedPassword, userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rowsAffected == 1) {
			// ✅ Update session if username was successfully updated
			user.setUsername(username);
			session.setAttribute("currentUser", user);

			// ✅ Redirect to /categoryService
			response.sendRedirect(request.getContextPath() + "/categoryService");
		} else {
			// ❌ Redirect with an error message
			response.sendRedirect(request.getContextPath()
					+ "/pages/accountSettings.jsp?error=Could not update user. Please try again.");
		}
	}

	private boolean validateAdminAccess(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return false;
		}

		if (!sessionUtils.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
			return false;
		}

		return true;
	}
}