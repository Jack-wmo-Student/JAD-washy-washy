package CONTROLLER;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import utils.sessionUtils;

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
            String deleteSQL = "DELETE FROM service WHERE service_id = ?";
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
        response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
    }
}