package CONTROLLER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import DBACCESS.DBConnection;
import MODEL.CLASS.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingStatusController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int bookingId = Integer.parseInt(request.getParameter("bookingId"));

			// Get booking status from database
			String query = """
					    SELECT s.status_name, b.booked_date, s2.service_name, p.payment_status
					    FROM booking b
					    JOIN status s ON b.status_id = s.status_id
					    JOIN service s2 ON b.service_id = s2.service_id
					    LEFT JOIN payment p ON b.booking_id = p.booking_id
					    WHERE b.booking_id = ?
					""";

			String sql = "INSERT INTO users (status_id, role_id, username, password) VALUES (?, ?, ?, ?)";

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				// Set default values for status and role (modify as needed)
				stmt.setInt(1, 1); // Default status_id
				stmt.setInt(2, role_id); // Default role_id
				stmt.setString(3, username);
				stmt.setString(4, password); // Consider hashing password before storing

				int affectedRows = stmt.executeUpdate();
				if (affectedRows > 0) {
					try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							int userId = generatedKeys.getInt(1);
							new User(userId, username, 1, 1); // Create User object
						}
					}
					return true;
				}
			}

			request.setAttribute("bookingStatus", status);
			request.getRequestDispatcher("/pages/bookingProgress.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("error.jsp");
		}
	}
}