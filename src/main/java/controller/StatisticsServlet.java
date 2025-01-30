package CONTROLLER;

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
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

public class StatisticsServlet extends HttpServlet {
	 private static final long serialVersionUID = 1L;
	 String dbClass = System.getenv("DB_CLASS");
     String dbUrl = System.getenv("DB_URL");
     String dbUser = System.getenv("DB_USER");
     String dbPassword = System.getenv("DB_PASSWORD");
   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
        	System.out.println("===== In Stats Servlet DoGet ======");
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
            request.getRequestDispatcher("/pages/statsDashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private int getTotalServices(Connection connection) throws SQLException {
    	System.out.println("--- getTotalServices ---");
        String query = "SELECT COUNT(*) AS total_services FROM service";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
            	System.out.println("total_services"+ rs.getInt("total_services"));
                return rs.getInt("total_services");
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getCategoryWiseServices(Connection connection) throws SQLException {
    	System.out.println("--- getCategoryWiseServices ---");
        String query = "SELECT category_id, COUNT(*) AS total_services FROM service GROUP BY category_id";
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("category_id", rs.getInt("category_id"));
                row.put("total_services", rs.getInt("total_services"));
                results.add(row);
                
                System.out.println("cat_id:" + rs.getInt("category_id"));
                System.out.println("total_services:" + rs.getInt("total_services"));
            }
        }
        return results;
    }

    private int getTotalBookings(Connection connection) throws SQLException {
    	System.out.println("--- getTotalBookings ---");
        String query = "SELECT COUNT(*) AS total_bookings FROM booking";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
            	System.out.println("tot_bookings:" + rs.getInt("total_bookings"));
                return rs.getInt("total_bookings");
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getMostBookedServices(Connection connection) throws SQLException {
    	System.out.println("--- getMostBookedServices ---");
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
                
                System.out.println("service_id:" + rs.getInt("service_id"));
                System.out.println("total_bookings:" + rs.getInt("total_bookings"));
                results.add(row);
            }
        }
        return results;
    }

    private List<Map<String, Object>> getBookingsByDate(Connection connection) throws SQLException {
    	System.out.println("--- getBookingsByDate ---");
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
                
                System.out.println("booked_date:" + rs.getDate("booked_date").toString());
                System.out.println("total_bookings:" + rs.getInt("total_bookings"));
                
                results.add(row);
            }
        }
        return results;
    }
}