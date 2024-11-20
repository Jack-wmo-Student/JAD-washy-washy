package backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.sql.*;


@WebServlet("/ServiceServlet")
public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Database configuration
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	private static final String DB_DRIVER = "org.postgresql.Driver";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String categoryId = request.getParameter("categoryId");
		request.getSession().setAttribute("categoryId", categoryId);

		List<service> services = new ArrayList<>();
		String categoryName = null;

		try (Connection conn = getConnection();
				PreparedStatement psCategory = conn.prepareStatement("SELECT category_name FROM service_categories WHERE category_id = ?");
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
					service service = new service(
							rsServices.getInt("service_id"),
							rsServices.getInt("category_id"), 
							rsServices.getString("service_name"),
							rsServices.getDouble("price"),
							rsServices.getInt("duration_in_hour"),
							rsServices.getString("service_description")
							);
					services.add(service);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		request.getSession().setAttribute("categoryName", categoryName);
		request.setAttribute("services", services);

		// Forward to the JSP
		request.getRequestDispatcher("editService.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String serviceName = request.getParameter("serviceName");
		String servicePrice = request.getParameter("servicePrice");
		String serviceDuration = request.getParameter("serviceDuration");
		String serviceDescription = request.getParameter("serviceDescription");
		String categoryId = (String) request.getSession().getAttribute("categoryId");

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO service (service_name, category_id, price, duration_in_hour, service_description) VALUES (?, ?, ?, ?, ?)")) {

			ps.setString(1, serviceName);
			ps.setInt(2, Integer.parseInt(categoryId));
			ps.setDouble(3, Double.parseDouble(servicePrice));
			ps.setInt(4, Integer.parseInt(serviceDuration));
			ps.setString(5, serviceDescription);
			ps.executeUpdate();

			request.setAttribute("successMessage", "Service added successfully!");

		} catch (Exception e) {
			request.setAttribute("errorMessage", "Error adding service: " + e.getMessage());
			e.printStackTrace();
		}

		// Redirect to refresh the service list
		response.sendRedirect("ServiceServlet?categoryId=" + categoryId);
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(DB_DRIVER);
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}
}
