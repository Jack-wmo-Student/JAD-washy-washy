package controller;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.category;
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
        	request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
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
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			// Database setup
			String dbClass = System.getenv("DB_CLASS");
			String dbUrl = System.getenv("DB_URL");
			String dbUser = System.getenv("DB_USER");
			String dbPassword = System.getenv("DB_PASSWORD");

			String categoryName = request.getParameter("categoryName");
			String categoryDescription = request.getParameter("categoryDescription");

			Class.forName(dbClass);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

			String insertSQL = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
			ps = conn.prepareStatement(insertSQL);
			ps.setString(1, categoryName);
			ps.setString(2, categoryDescription);
			ps.executeUpdate();

			// Add success message
			request.getSession().setAttribute("successMessage", "Category added successfully!");

		} catch (Exception e) {
			request.getSession().setAttribute("errorMessage", "Error adding category: " + e.getMessage());
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redirect to fetch categories again
		response.sendRedirect(request.getContextPath() + "/CreateCategoryServlet");
	}
}