package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;

/**
 * Servlet implementation class logout
 */
public class logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public logout() {
		super();
	}

	/**
	 * Handles the HTTP GET request to log out the user.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get the current session if it exists
		HttpSession session = request.getSession(false);

		if (session != null) {
			@SuppressWarnings("unchecked")
			Map<Category, List<Service>> categoryServiceMap = (Map<Category, List<Service>>) session
					.getAttribute("categoryServiceMap");

			// Invalidate the current session
			session.invalidate();

			// Create a new session and restore the preserved attributes
			session = request.getSession(true);
			if (categoryServiceMap != null) {
				session.setAttribute("categoryServiceMap", categoryServiceMap);
			}
		}

		// Clear the isLoggedIn cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("isLoggedIn".equals(cookie.getName())) {
					cookie.setValue(""); // Clear the value
					cookie.setMaxAge(0); // Invalidate the cookie immediately
					cookie.setPath("/"); // Ensure the path matches the cookie's original path
					cookie.setSecure(false); // Ensure it's only sent over HTTPS
					cookie.setHttpOnly(true); // Prevent JavaScript access
					response.addCookie(cookie); // Add the cleared cookie to the response
				}
			}
		}
		// Prevent caching
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// Redirect to home page
		response.sendRedirect(request.getContextPath() + "/pages/homePage.jsp"); // Adjust the URL as per your
																					// application's login page
	}

	/**
	 * Handles the HTTP POST request to log out the user.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Call doGet to handle logout
		doGet(request, response);
	}
}
