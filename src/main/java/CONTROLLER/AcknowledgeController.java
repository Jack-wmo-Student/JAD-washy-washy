package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import MODEL.DAO.BookingDAO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Servlet implementation class AcknowledgeController
 */
public class AcknowledgeController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AcknowledgeController() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			out.print("{\"error\": \"Session Expired. Please log in again.\"}");
			out.flush();
			return;
		}

		BookingDAO bookingDAO = new BookingDAO();
		Map<String, Integer> bookingServiceStatusId;
		Map<String, Object> bookingDetails;

		try {
			// Retrieve the tracked service from session
			bookingServiceStatusId = (Map<String, Integer>) session.getAttribute("currentTrackedService");
			if (bookingServiceStatusId == null || !bookingServiceStatusId.containsKey("service_id")
					|| !bookingServiceStatusId.containsKey("booking_id")) {
				System.out.println(bookingServiceStatusId);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\": \"Invalid session data.\"}");
				out.flush();
				return;
			}

			// Fetch booking details
			bookingDetails = bookingDAO.getBookingDetails(bookingServiceStatusId.get("service_id"),
					bookingServiceStatusId.get("booking_id"));

			if (bookingDetails == null || !bookingDetails.containsKey("price")
					|| !bookingDetails.containsKey("username") || !bookingDetails.containsKey("password")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\": \"Booking details not found.\"}");
				out.flush();
				return;
			}

			System.out.println("Booking Details: " + bookingDetails);

			// Create request body as a list
			List<Map<String, Object>> requestBodyList = new ArrayList<>();
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("bookingId", bookingServiceStatusId.get("booking_id"));
			requestBody.put("amount", bookingDetails.get("price"));
			requestBody.put("currency", "SGD");
			requestBody.put("quantity", 1);
			requestBodyList.add(requestBody);

			// Set up HTTP client
			Client client = ClientBuilder.newClient();
			String restUrl = "https://jad-wapi-wapi-ca2.onrender.com/wapi-wapi/payments/checkout";
			WebTarget target = client.target(restUrl);
			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);

			// Set headers
			invocationBuilder.header("X-Username", bookingDetails.get("username"));
			invocationBuilder.header("X-Secret", bookingDetails.get("password"));
			invocationBuilder.header("X-third-party", "false");

			// Send request
			Response resp = invocationBuilder.post(Entity.entity(requestBodyList, MediaType.APPLICATION_JSON));

			// Read API response
			String jsonResponse = resp.readEntity(String.class);
			System.out.println("Status: " + resp.getStatus());
			System.out.println("Response Body: " + jsonResponse);

			// Convert JSON response into a map
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(jsonResponse,
					new TypeReference<Map<String, Object>>() {
					});

			if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
				System.out.println("Payment session created successfully.");

				if (responseMap.containsKey("sessionUrl")) {
					String paymentUrl = (String) responseMap.get("sessionUrl");

					// Clear tracked service after checkout starts
					session.removeAttribute("currentTrackedService");

					System.out.println("Returning redirect URL to client: " + paymentUrl);
					response.setStatus(HttpServletResponse.SC_OK);
					out.print("{\"redirectUrl\": \"" + paymentUrl + "\"}");
					out.flush();
				} else {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					out.print("{\"error\": \"Invalid response from payment service.\"}");
					out.flush();
				}
			} else {
				System.out.println("Payment processing failed.");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\": \"Payment processing failed.\"}");
				out.flush();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\": \"Internal error: " + e.getMessage() + "\"}");
			out.flush();
		}
	}
}
