package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeleteCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbClass = "DB_CLASS"; // Replace with your database driver class
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        Connection conn = null;
        PreparedStatement ps = null;

        // Get the category ID from the request
        String categoryId = request.getParameter("categoryId");

        try {
            // Load database driver
            Class.forName(dbClass);

            // Establish connection
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // SQL to delete the category
            String deleteSQL = "DELETE FROM service_category WHERE category_id = ?";
            ps = conn.prepareStatement(deleteSQL);
            ps.setInt(1, Integer.parseInt(categoryId));
            
            // Execute the delete query
            int rowsDeleted = ps.executeUpdate();

            // Set a success or error message as a request attribute
            if (rowsDeleted > 0) {
                request.setAttribute("message", "Category deleted successfully!");
            } else {
                request.setAttribute("message", "Category not found or could not be deleted.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error deleting category: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Forward the request back to the categories page with a message
        request.getRequestDispatcher("editServiceCategory.jsp").forward(request, response);
    }
}