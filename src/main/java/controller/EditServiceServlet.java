package controller;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;
import model.service;
import java.util.*;

public class EditServiceServlet extends HttpServlet {

	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String categoryId = request.getParameter("categoryId"); // Get the dynamic categoryId
		HttpSession session = request.getSession(false);

		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null || session.getAttribute("userId") == null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}

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

		// Fetch services for the given categoryId
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "SELECT * FROM service WHERE category_id = ?";
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setInt(1, Integer.parseInt(categoryId));
				try (ResultSet rs = ps.executeQuery()) {
					List<service> services = new ArrayList<>();
					while (rs.next()) {
						services.add(new service(
								rs.getInt("service_id"),
								rs.getInt("category_id"),
								rs.getString("service_name"),
								rs.getDouble("price"),
								rs.getInt("duration_in_hour"),
								rs.getString("service_description")
								));
					}
					session.setAttribute("services", services); // Store services in the session
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Error fetching services for category.");
		}
		System.out.println("Category ID: " + categoryId);
		// Forward to the editService page with the categoryId
		response.sendRedirect(request.getContextPath() + "/pages/editService.jsp?categoryId=" + categoryId);
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
		response.sendRedirect(request.getContextPath() + "/EditServiceServlet");
	}
}