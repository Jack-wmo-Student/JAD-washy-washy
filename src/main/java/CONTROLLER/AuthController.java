package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;

import MODEL.CLASS.User;
import MODEL.DAO.CategoryServiceDAO;
import MODEL.DAO.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.passwordUtils;

public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AuthController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// Hash the password
		String hashedPassword = passwordUtils.hashPassword(password);

		// Validate the user using UserDAO
		UserDAO userDAO = new UserDAO();
		User validatedUser = userDAO.validateUser(username, hashedPassword);

		if (validatedUser != null) {
			if (validatedUser.isIsBlocked()) {
				// Handle blocked user
				request.setAttribute("error", "Your account is blocked. Please contact support.");
				request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			} else {
				
				if (validatedUser.isIsAdmin()) {
					Cookie isAdminCookie = new Cookie("isAdmin", "true");
					isAdminCookie.setPath("/"); // Cookie is valid for the entire domain
					isAdminCookie.setHttpOnly(true); // Prevent JavaScript access
					isAdminCookie.setSecure(false); // Ensure it's sent only over HTTPS
					isAdminCookie.setMaxAge(60 * 60);
					response.addCookie(isAdminCookie);
				}
				
				// Set a lightweight cookie for session validation
				Cookie isLoggedInCookie = new Cookie("isLoggedIn", "true");
				isLoggedInCookie.setPath(request.getContextPath()); // Cookie is valid for the entire domain
				isLoggedInCookie.setHttpOnly(true); // Prevent JavaScript access
				isLoggedInCookie.setSecure(false); // Ensure it's sent only over HTTPS
				isLoggedInCookie.setMaxAge(60 * 60); // Cookie expiry: 1 hour

				response.addCookie(isLoggedInCookie);

				// Use session attributes for sensitive user data
				HttpSession session = request.getSession();
				// Restore categoryServiceMap if it's missing
				session.setAttribute("currentUser", validatedUser);
				session.setAttribute("isAdmin", validatedUser.isIsAdmin());

				// Redirect to homePage
				response.sendRedirect(request.getContextPath() + "/categoryService");
			}
		} else {
			// Handle invalid login
			request.setAttribute("error", "Invalid username or password.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
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
		UserDAO userDAO = new UserDAO();
		try {
			if (userDAO.isUsernameExists(username)) {
				request.setAttribute("error", "Username already exists!");
				request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
				return;
			}
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Hash the password
		String hashedPassword = passwordUtils.hashPassword(password);
		// Save user to the database
		boolean isRegistered = false;
		try {
			isRegistered = userDAO.CreateUser(username, hashedPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isRegistered) {
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
		} else {
			request.setAttribute("error", "Registration failed. Try again.");
			request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
		}
	}
}
