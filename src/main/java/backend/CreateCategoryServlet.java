package backend;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

public class CreateCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// Database setup
			String dbClass = System.getenv("DB_CLASS");
			String dbUrl = System.getenv("DB_URL");
			String dbUser = System.getenv("DB_USER");
			String dbPassword = System.getenv("DB_PASSWORD");

			if (dbClass == null || dbUrl == null || dbUser == null || dbPassword == null) {
				throw new ServletException("Database credentials are missing!");
			}

			// Load driver and connect
			Class.forName(dbClass);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

			// Fetch categories
			String fetchSQL = "SELECT category_id, category_name, category_description FROM category";
			ps = conn.prepareStatement(fetchSQL);
			rs = ps.executeQuery();

			List<category> categories = new ArrayList<>();
			while (rs.next()) {
				categories.add(new category(
						rs.getInt("category_id"),
						rs.getString("category_name").trim(),
						rs.getString("category_description").trim()
						));
			}

			// Attach categories to the request
			request.setAttribute("categories", categories);

		} catch (Exception e) {
			request.setAttribute("errorMessage", "Error fetching categories: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redirect to JSP
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