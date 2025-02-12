package CONTROLLER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SetTrackedService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("currentUser") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\": \"Session Expired. Please log in again.\"}");
			return;
		}

		// Read JSON data from request body
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			JsonReader jsonReader = Json.createReader(reader);
			JsonObject jsonData = jsonReader.readObject();

			// Extract values
			String bookingIdStr = jsonData.getString("bookingId", "").trim();
			String userIdStr = jsonData.getString("userId", "").trim();
			String serviceIdStr = jsonData.getString("serviceId", "").trim();

			// Debugging logs
			System.out.println("Received JSON Parameters - bookingId: " + bookingIdStr + ", userId: " + userIdStr
					+ ", serviceId: " + serviceIdStr);

			if (bookingIdStr.isEmpty() || userIdStr.isEmpty() || serviceIdStr.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(
						"{\"error\": \"Invalid request parameters: bookingId, userId, or serviceId is missing.\"}");
				return;
			}

			try {
				int bookingId = Integer.parseInt(bookingIdStr);
				int userId = Integer.parseInt(userIdStr);
				int serviceId = Integer.parseInt(serviceIdStr);

				// Store tracked service in session
				Map<String, Integer> trackedService = new HashMap<>();
				trackedService.put("booking_id", bookingId);
				trackedService.put("user_id", userId);
				trackedService.put("service_id", serviceId);
				session.setAttribute("currentTrackedService", trackedService);

				// Forward the request to AcknowledgeServlet
				RequestDispatcher dispatcher = request.getRequestDispatcher("/AcknowledgeServlet");
				dispatcher.forward(request, response);

			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("{\"error\": \"Invalid number format for request parameters.\"}");
				e.printStackTrace();
			}

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"error\": \"Error processing JSON request.\"}");
			e.printStackTrace();
		}
	}
}
