package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.ServiceStatistics;
import MODEL.DAO.StatisticsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.sessionUtils;

public class ServiceStatisticsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final StatisticsDAO statisticsDAO = new StatisticsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Validate admin access
            if (!validateAdminAccess(request, response)) {
                return;
            }

            System.out.println("===== In Stats Servlet DoGet ======");

            // Get timeframe parameter, default to "this month"
            String timeframe = request.getParameter("timeframe");
            if (timeframe == null || timeframe.trim().isEmpty()) {
                timeframe = "this month";
            }

            // Fetch legacy statistics
            int totalServices = statisticsDAO.getTotalServices();
            List<Map<String, Object>> categoryWiseServices = statisticsDAO.getCategoryWiseServices();
            int totalBookings = statisticsDAO.getTotalBookings();
            List<ServiceStatistics> mostBookedServices = statisticsDAO.getMostBookedServices();
            List<Map<String, Object>> bookingsByDate = statisticsDAO.getBookingsByDate();

            // Fetch new profitability statistics
            List<ServiceStatistics> mostProfitable = statisticsDAO.getMostProfitableServices(timeframe);
            List<ServiceStatistics> leastProfitable = statisticsDAO.getLeastProfitableServices(timeframe);
            List<ServiceStatistics> bookingsPerService = statisticsDAO.getBookingsPerService(timeframe);

            // Set legacy attributes
            request.setAttribute("totalServices", totalServices);
            request.setAttribute("categoryWiseServices", categoryWiseServices);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("mostBookedServices", mostBookedServices);
            request.setAttribute("bookingsByDate", bookingsByDate);

            // Set new statistics attributes
            request.setAttribute("timeframe", timeframe);
            request.setAttribute("mostProfitableServices", mostProfitable);
            request.setAttribute("leastProfitableServices", leastProfitable);
            request.setAttribute("bookingsPerService", bookingsPerService);

            // Debug output
            System.out.println("Total Services: " + totalServices);
            System.out.println("Total Bookings: " + totalBookings);
            System.out.println("Most Profitable Services: " + (mostProfitable != null ? mostProfitable.size() : "null"));
            System.out.println("Least Profitable Services: " + (leastProfitable != null ? leastProfitable.size() : "null"));
            System.out.println("Bookings Per Service: " + (bookingsPerService != null ? bookingsPerService.size() : "null"));

            // Forward to JSP
            request.getRequestDispatcher("/pages/statsDashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.out.println("Error in StatisticsServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error retrieving statistics: " + e.getMessage());
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
        }
    }

    private boolean validateAdminAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
            request.setAttribute("error", "You must log in first.");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return false;
        }

        if (!sessionUtils.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
            return false;
        }

        return true;
    }
}