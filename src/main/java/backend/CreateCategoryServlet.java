package backend;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
			String dbPassword = System.getenv("DB_PASSWORD");
			String dbUser = System.getenv("DB_USER");

			// Connect to the database and fetch categories
			Class.forName(dbClass);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

			String fetchSQL = "SELECT * FROM category";
			ps = conn.prepareStatement(fetchSQL);
			rs = ps.executeQuery();

			List<category> categories = new ArrayList<>();
			while (rs.next()) {
				category category = new category(
						rs.getInt("category_id"),
						rs.getString("category_name"),
						rs.getString("category_description")
						);
				categories.add(category);
			}

			// Attach categories to the request
			request.setAttribute("categories", categories);

		} catch (Exception e) {
			request.setAttribute("errorMessage", "Error fetching categories: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Forward to JSP
		request.getRequestDispatcher("/CreateCategoryServlet.jsp").forward(request, response);
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
			String dbPassword = System.getenv("DB_PASSWORD");
			String dbUser = System.getenv("DB_USER");

			// Connect to the database and insert a new category
			String categoryName = request.getParameter("categoryName");
			String categoryDescription = request.getParameter("categoryDescription");

			Class.forName(dbClass);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

			String insertSQL = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
			ps = conn.prepareStatement(insertSQL);
			ps.setString(1, categoryName);
			ps.setString(2, categoryDescription);
			ps.executeUpdate();

			// Add success message to the request
			request.setAttribute("successMessage", "Category added successfully!");

		} catch (Exception e) {
			request.setAttribute("errorMessage", "Error adding category: " + e.getMessage());
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redirect to fetch categories again
		response.sendRedirect("createCategory");
	}
}