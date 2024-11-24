package controller;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StatisticsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/your_database";
    private static final String DB_USER = "your_user";
    private static final String DB_PASSWORD = "your_password";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Fetch total services
            int totalServices = getTotalServices(connection);

            // Fetch category-wise service distribution
            List<Map<String, Object>> categoryWiseServices = getCategoryWiseServices(connection);

            // Fetch total bookings
            int totalBookings = getTotalBookings(connection);

            // Fetch most booked services
            List<Map<String, Object>> mostBookedServices = getMostBookedServices(connection);

            // Fetch bookings by date
            List<Map<String, Object>> bookingsByDate = getBookingsByDate(connection);

            // Set attributes for JSP
            request.setAttribute("totalServices", totalServices);
            request.setAttribute("categoryWiseServices", categoryWiseServices);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("mostBookedServices", mostBookedServices);
            request.setAttribute("bookingsByDate", bookingsByDate);

            // Forward to JSP
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private int getTotalServices(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) AS total_services FROM service";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_services");
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getCategoryWiseServices(Connection connection) throws SQLException {
        String query = "SELECT category_id, COUNT(*) AS total_services FROM service GROUP BY category_id";
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("category_id", rs.getInt("category_id"));
                row.put("total_services", rs.getInt("total_services"));
                results.add(row);
            }
        }
        return results;
    }

    private int getTotalBookings(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) AS total_bookings FROM booking";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_bookings");
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getMostBookedServices(Connection connection) throws SQLException {
        String query = """
            SELECT service_id, COUNT(*) AS total_bookings
            FROM booking
            GROUP BY service_id
            ORDER BY total_bookings DESC
            LIMIT 5
        """;
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("service_id", rs.getInt("service_id"));
                row.put("total_bookings", rs.getInt("total_bookings"));
                results.add(row);
            }
        }
        return results;
    }

    private List<Map<String, Object>> getBookingsByDate(Connection connection) throws SQLException {
        String query = """
            SELECT booked_date, COUNT(*) AS total_bookings 
            FROM booking 
            GROUP BY booked_date 
            ORDER BY booked_date ASC
        """;
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("booked_date", rs.getDate("booked_date").toString());
                row.put("total_bookings", rs.getInt("total_bookings"));
                results.add(row);
            }
        }
        return results;
    }
}