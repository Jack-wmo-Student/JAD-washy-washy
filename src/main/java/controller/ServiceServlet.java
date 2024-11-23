package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.service;
import model.category;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;

import utils.sessionUtils;



public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Database configuration
	String dbClass = System.getenv("DB_CLASS");
    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");
//    Class.forName(dbClass);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

		String categoryId = request.getParameter("categoryId");
		request.getSession().setAttribute("categoryId", categoryId);

		List<service> services = new ArrayList<>();
		String categoryName = null;

		try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
				PreparedStatement psCategory = conn
						.prepareStatement("SELECT category_name FROM service_categories WHERE category_id = ?");
				PreparedStatement psServices = conn.prepareStatement("SELECT * FROM service WHERE category_id = ?")) {

			psCategory.setInt(1, Integer.parseInt(categoryId));
			try (ResultSet rsCategory = psCategory.executeQuery()) {
				if (rsCategory.next()) {
					categoryName = rsCategory.getString("category_name");
				}
			}

			psServices.setInt(1, Integer.parseInt(categoryId));
			try (ResultSet rsServices = psServices.executeQuery()) {
				while (rsServices.next()) {
					service service = new service(rsServices.getInt("service_id"), rsServices.getInt("category_id"),
							rsServices.getString("service_name"), rsServices.getDouble("price"),
							rsServices.getInt("duration_in_hour"), rsServices.getString("service_description"));
					services.add(service);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		response.sendRedirect(request.getContextPath() + "/pages/editService.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    String serviceName = request.getParameter("serviceName");
	    String servicePrice = request.getParameter("servicePrice");
	    String serviceDuration = request.getParameter("serviceDuration");
	    String serviceDescription = request.getParameter("serviceDescription");
	    String categoryId = (String) request.getSession().getAttribute("categoryId");

	    HttpSession session = request.getSession();
	    int generatedServiceId = 0;

	    categoryId = request.getParameter("categoryId");
	    try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	         PreparedStatement psInsert = conn.prepareStatement(
	                 "INSERT INTO service (service_name, category_id, price, duration_in_hour, service_description) VALUES (?, ?, ?, ?, ?)",
	                 Statement.RETURN_GENERATED_KEYS);
	         PreparedStatement psFetchServices = conn.prepareStatement(
	                 "SELECT * FROM service WHERE category_id = ?")) {

	        // Insert the new service into the database
	        psInsert.setString(1, serviceName);
	        psInsert.setInt(2, Integer.parseInt(categoryId));
	        psInsert.setDouble(3, Double.parseDouble(servicePrice));
	        psInsert.setInt(4, Integer.parseInt(serviceDuration));
	        psInsert.setString(5, serviceDescription);
	        int rowsAffected = psInsert.executeUpdate();

	        // Retrieve the generated service ID
	        if (rowsAffected > 0) {
	            try (ResultSet rs = psInsert.getGeneratedKeys()) {
	                if (rs.next()) {
	                    generatedServiceId = rs.getInt(1);
	                }
	            }
	        }

	        // Fetch updated services for the category
	        psFetchServices.setInt(1, Integer.parseInt(categoryId));
	        List<service> updatedServices = new ArrayList<>();
	        try (ResultSet rsServices = psFetchServices.executeQuery()) {
	            while (rsServices.next()) {
	                service service = new service(
	                        rsServices.getInt("service_id"),
	                        rsServices.getInt("category_id"),
	                        rsServices.getString("service_name"),
	                        rsServices.getDouble("price"),
	                        rsServices.getInt("duration_in_hour"),
	                        rsServices.getString("service_description"));
	                updatedServices.add(service);
	            }
	        }

	        // Update the sessionCategoryServiceMap
	        @SuppressWarnings("unchecked")
	        Map<category, List<service>> sessionCategoryServiceMap = 
	                (Map<category, List<service>>) session.getAttribute("categoryServiceMap");

	        if (sessionCategoryServiceMap != null) {
	            // Locate the relevant category and update its services
	            for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
	                category cat = entry.getKey();
	                if (cat.getId() == Integer.parseInt(categoryId)) {
	                    entry.setValue(updatedServices); // Update the services for this category
	                    break;
	                }
	            }

	            // Update the session attribute
	            session.setAttribute("categoryServiceMap", sessionCategoryServiceMap);
	        }

	        // Add success message
	        session.setAttribute("successMessage", "Service added successfully!");

	    } catch (Exception e) {
	        session.setAttribute("errorMessage", "Error adding service: " + e.getMessage());
	        e.printStackTrace();
	    }

	    // Redirect to the service list page to refresh the display
	    response.sendRedirect(request.getContextPath() + "/ServiceServlet");
	}
}