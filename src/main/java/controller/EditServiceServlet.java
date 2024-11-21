package controller;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class EditServiceServlet extends HttpServlet {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String serviceId = request.getParameter("serviceId");
        HttpSession session = request.getSession();
        
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

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM service WHERE service_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, Integer.parseInt(serviceId));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Store service details in session attributes
                        session.setAttribute("serviceName", rs.getString("service_name"));
                        session.setAttribute("servicePrice", rs.getDouble("price"));
                        session.setAttribute("serviceDuration", rs.getInt("duration_in_hour"));
                        session.setAttribute("serviceDescription", rs.getString("service_description"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error fetching service details.");
        }

        // Forward to the JSP
        request.getRequestDispatcher("/editService.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String serviceId = request.getParameter("serviceId");
        String returnUrl = request.getParameter("returnUrl");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE service SET service_name = ?, price = ?, duration_in_hour = ?, service_description = ? WHERE service_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, request.getParameter("serviceName"));
                ps.setDouble(2, Double.parseDouble(request.getParameter("servicePrice")));
                ps.setInt(3, Integer.parseInt(request.getParameter("serviceDuration")));
                ps.setString(4, request.getParameter("serviceDescription"));
                ps.setInt(5, Integer.parseInt(serviceId));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error updating service details.");
            request.getRequestDispatcher("/editService.jsp").forward(request, response);
            return;
        }

        // Redirect after successful update
        response.sendRedirect(returnUrl != null ? returnUrl : "/serviceList?categoryId=" + request.getParameter("categoryId"));
    }
}