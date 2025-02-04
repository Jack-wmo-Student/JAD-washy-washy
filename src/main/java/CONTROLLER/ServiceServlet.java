package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;

public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Database configuration
	String dbClass = System.getenv("DB_CLASS");
	String dbUrl = System.getenv("DB_URL");
	String dbUser = System.getenv("DB_USER");
	String dbPassword = System.getenv("DB_PASSWORD");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
			return;
		}

		// Optional: Check if the user is an admin
		if (!sessionUtils.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
			return;
		}

		String categoryIdParam = request.getParameter("categoryId");
		if (categoryIdParam == null || categoryIdParam.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
			return;
		}

		int categoryId = Integer.parseInt(categoryIdParam);
		List<Service> services = new ArrayList<>();
		String categoryName = null;

		// Check if the request has already been forwarded
		Boolean isRedirected = (Boolean) request.getAttribute("redirected");
		if (isRedirected == null) {
			request.setAttribute("redirected", true); // Mark as forwarded
			request.getRequestDispatcher("/pages/editServiceServlet").forward(request, response);
			return;
		}

		// Fetch category and services
		try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
				PreparedStatement psCategory = conn
						.prepareStatement("SELECT category_name FROM service_categories WHERE category_id = ?");
				PreparedStatement psServices = conn.prepareStatement("SELECT * FROM service WHERE category_id = ?")) {

			psCategory.setInt(1, categoryId);
			try (ResultSet rsCategory = psCategory.executeQuery()) {
				if (rsCategory.next()) {
					categoryName = rsCategory.getString("category_name");
				}
			}

			psServices.setInt(1, categoryId);
			try (ResultSet rsServices = psServices.executeQuery()) {
				while (rsServices.next()) {
					services.add(new Service(rsServices.getInt("service_id"), rsServices.getInt("category_id"),
							rsServices.getString("service_name"), rsServices.getDouble("price"),
							rsServices.getInt("duration_in_hour"), rsServices.getString("service_description")));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Pass data to JSP
		request.setAttribute("categoryName", categoryName);
		request.setAttribute("services", services);
		request.setAttribute("categoryId", categoryId);
		response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String serviceName = request.getParameter("serviceName");
		String servicePrice = request.getParameter("servicePrice");
		String serviceDuration = request.getParameter("serviceDuration");
		String serviceDescription = request.getParameter("serviceDescription");
		String categoryIdParam = request.getParameter("categoryId");

		if (categoryIdParam == null || categoryIdParam.isEmpty()) {
			session.setAttribute("errorMessage", "Invalid categoryId provided.");
			response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
			return;
		}

		int categoryId = Integer.parseInt(categoryIdParam);
		System.out.print(categoryIdParam);

		try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
				PreparedStatement psInsert = conn.prepareStatement(
						"INSERT INTO service (service_name, category_id, price, duration_in_hour, service_description) VALUES (?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				PreparedStatement psFetchServices = conn
						.prepareStatement("SELECT * FROM service WHERE category_id = ?")) {

			// Insert the new service into the database
			psInsert.setString(1, serviceName);
			psInsert.setInt(2, categoryId);
			psInsert.setDouble(3, Double.parseDouble(servicePrice));
			psInsert.setInt(4, Integer.parseInt(serviceDuration));
			psInsert.setString(5, serviceDescription);
			int rowsAffected = psInsert.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Service added successfully.");
			}

			// Fetch updated services for the category
			psFetchServices.setInt(1, categoryId);
			List<Service> updatedServices = new ArrayList<>();
			try (ResultSet rsServices = psFetchServices.executeQuery()) {
				while (rsServices.next()) {
					updatedServices.add(new Service(rsServices.getInt("service_id"), rsServices.getInt("category_id"),
							rsServices.getString("service_name"), rsServices.getDouble("price"),
							rsServices.getInt("duration_in_hour"), rsServices.getString("service_description")));
				}
			}

			// Debug: Print updated services
			System.out.println("Updated services: " + updatedServices);

			// Update the sessionCategoryServiceMap
			@SuppressWarnings("unchecked")
			Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
					.getAttribute("categoryServiceMap");

			if (sessionCategoryServiceMap != null) {
				for (Map.Entry<Category, List<Service>> entry : sessionCategoryServiceMap.entrySet()) {
					Category cat = entry.getKey();
					if (cat.getId() == categoryId) {
						entry.setValue(updatedServices); // Update the services for this category
						break;
					}
				}
				session.setAttribute("categoryServiceMap", sessionCategoryServiceMap);
			}

			// Add success message
			session.setAttribute("successMessage", "Service added successfully!");

		} catch (Exception e) {
			session.setAttribute("errorMessage", "Error adding service: " + e.getMessage());
			e.printStackTrace();
		}
		// Redirect back to the category's services page
		response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
	}
}