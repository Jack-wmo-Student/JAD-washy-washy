package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
            // Check if user is logged in and is admin
            if (!validateAdminAccess(request, response)) {
                return;
            }

            // Set default timeframe
            String timeframe = "this month";
            
            // Get timeframe from request if present
            String requestTimeframe = request.getParameter("timeframe");
            if (requestTimeframe != null && !requestTimeframe.trim().isEmpty()) {
                timeframe = requestTimeframe.trim().toLowerCase();
            }
            
            // Validate timeframe
            if (!isValidTimeframe(timeframe)) {
                timeframe = "this month";
            }

            // Get statistics
            List<ServiceStatistics> mostProfitable = statisticsDAO.getMostProfitableServices(timeframe);
            List<ServiceStatistics> leastProfitable = statisticsDAO.getLeastProfitableServices(timeframe);
            List<ServiceStatistics> bookingsPerService = statisticsDAO.getBookingsPerService(timeframe);

            // Set attributes
            request.setAttribute("timeframe", timeframe);
            request.setAttribute("mostProfitableServices", mostProfitable);
            request.setAttribute("leastProfitableServices", leastProfitable);
            request.setAttribute("bookingsPerService", bookingsPerService);

            // Forward to JSP
            request.getRequestDispatcher("/pages/statsDashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            System.out.println("Error in ServiceStatisticsServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error retrieving statistics: " + e.getMessage());
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
        }
    }

    private boolean isValidTimeframe(String timeframe) {
		// TODO Auto-generated method stub
		return false;
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