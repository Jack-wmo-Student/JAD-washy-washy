package CONTROLLER;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.sessionUtils;

public class CreateCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

		// Optional: Check if the user is an admin
		if (!sessionUtils.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
			return;
		}

		response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

		// Optional: Check if the user is an admin
		if (!sessionUtils.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
			return;
		}

		Connection conn = null;
		PreparedStatement ps = null;

		HttpSession session = request.getSession(false);

		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null
				|| session.getAttribute("userId") == null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

		int generatedId = 0;

		try {
			// Database setup
			String dbClass = System.getenv("DB_CLASS");
			String dbUrl = System.getenv("DB_URL");
			String dbUser = System.getenv("DB_USER");
			String dbPassword = System.getenv("DB_PASSWORD");

			String categoryName = request.getParameter("categoryName");
			String categoryDescription = request.getParameter("categoryDescription");

			// Load database driver and establish connection
			Class.forName(dbClass);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

			// Prepare the SQL query with RETURN_GENERATED_KEYS
			String insertSQL = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
			ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, categoryName);
			ps.setString(2, categoryDescription);

			// Execute insert
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				// Retrieve the generated key
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						generatedId = rs.getInt(1); // Get the generated ID
						System.out.println("Inserted row ID: " + generatedId);
					}
				}
			} else {
				System.out.println("Insert failed, no rows affected.");
			}

			// Retrieve the existing category-service map from the session
			@SuppressWarnings("unchecked")
			Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
					.getAttribute("categoryServiceMap");

			if (sessionCategoryServiceMap == null) {
				// Initialize a new map if it doesn't exist
				sessionCategoryServiceMap = new HashMap<>();
			}

			// Create a new category object and add it to the map
			Category newCategory = new Category(generatedId, categoryName, categoryDescription);
			sessionCategoryServiceMap.put(newCategory, new ArrayList<>());

			// Update the session attribute
			session.setAttribute("categoryServiceMap", sessionCategoryServiceMap);

			// Add success message
			session.setAttribute("successMessage", "Category added successfully!");

		} catch (Exception e) {
			session.setAttribute("errorMessage", "Error adding category: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redirect to the category page
		response.sendRedirect(request.getContextPath() + "/CreateCategoryServlet");
	}
}