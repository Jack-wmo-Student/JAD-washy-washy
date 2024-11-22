package controller;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

public class DeleteServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials (best practice: load from environment variables or configuration file)
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_CLASS = System.getenv("DB_CLASS");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serviceId = request.getParameter("serviceId");

        if (serviceId == null || serviceId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Service ID is required.");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // Load the database driver
            Class.forName(DB_CLASS);

            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to delete the service
            String deleteSQL = "DELETE FROM services WHERE service_id = ?";
            ps = conn.prepareStatement(deleteSQL);
            ps.setInt(1, Integer.parseInt(serviceId));

            // Execute the deletion
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                request.setAttribute("successMessage", "Service deleted successfully.");
            } else {
                request.setAttribute("errorMessage", "Service not found or could not be deleted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error deleting service: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Redirect or forward to a response page
        request.getRequestDispatcher("editServices.jsp").forward(request, response);
    }
}