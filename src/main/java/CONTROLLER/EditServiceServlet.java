package CONTROLLER;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

import java.util.*;

import MODEL.CLASS.Service;

public class EditServiceServlet extends HttpServlet {

	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		// Validate session and permissions
		if (session == null || !sessionUtils.isLoggedIn(request, "isLoggedIn")
				|| session.getAttribute("userId") == null) {
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

		if (!sessionUtils.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
			return;
		}

		String categoryId = request.getParameter("categoryId");
		if (categoryId == null || categoryId.isEmpty()) {
			request.setAttribute("errorMessage", "Invalid category ID.");
			request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
			return;
		}

		// Fetch services for the category
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "SELECT * FROM service WHERE category_id = ?";
			List<Service> services = new ArrayList<>();

			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setInt(1, Integer.parseInt(categoryId));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						services.add(new Service(rs.getInt("service_id"), rs.getInt("category_id"),
								rs.getString("service_name"), rs.getDouble("price"), rs.getInt("duration_in_hour"),
								rs.getString("service_description")));
					}
				}
			}

			// Store services and categoryId in the request scope
			request.setAttribute("services", services);
			request.setAttribute("categoryId", categoryId);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Error fetching services for the category.");
		}

		// Forward to JSP for editing
		response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String service_id = request.getParameter("serviceId");
		String category_id = request.getParameter("categoryId");
		String duration = request.getParameter("serviceDuration");
		String description = request.getParameter("serviceDescription");

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "UPDATE service SET service_name = ?, price = ?, duration_in_hour = ?, service_description = ? WHERE service_id = ?";
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setString(1, request.getParameter("serviceName"));
				ps.setDouble(2, Double.parseDouble(request.getParameter("servicePrice")));
				ps.setInt(3, Integer.parseInt(request.getParameter("serviceDuration")));
				ps.setString(4, request.getParameter("serviceDescription"));
				ps.setInt(5, Integer.parseInt(service_id));
				ps.executeUpdate();
			}

			// Success message
			request.setAttribute("successMessage", "Service updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Error updating service details.");
			request.getRequestDispatcher("/pages/editService.jsp").forward(request, response);
			return;
		}

		// Redirect back to the category's services page
		response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
	}
}