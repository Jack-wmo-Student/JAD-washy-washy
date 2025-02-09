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

import MODEL.DAO.BookingDAO;
import jakarta.servlet.RequestDispatcher;
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

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AcknowledgeController() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Client client = ClientBuilder.newClient();
		String restUrl = "https://jad-wapi-wapi-ca2.onrender.com/wapi-wapi/payments/checkout";
		WebTarget target = client.target(restUrl);
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);

		HttpSession session = request.getSession(false);
		if (session == null) {
			request.setAttribute("err", "Session Expired. Please log in again.");
			RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
			rd.forward(request, response);
			return;
		}

		Map<String, Integer> booking_service_status_id;
		BookingDAO bookingDAO = new BookingDAO();
		Map<String, Object> bookingDetails;

		try {
			booking_service_status_id = (Map<String, Integer>) session.getAttribute("currentTrackedService");
			if (booking_service_status_id == null || !booking_service_status_id.containsKey("service_id")
					|| !booking_service_status_id.containsKey("booking_id")) {
				request.setAttribute("err", "Invalid session data.");
				RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
				rd.forward(request, response);
				return;
			}

			bookingDetails = bookingDAO.getBookingDetails(booking_service_status_id.get("service_id"),
					booking_service_status_id.get("booking_id"));

			if (bookingDetails == null || !bookingDetails.containsKey("price")
					|| !bookingDetails.containsKey("username") || !bookingDetails.containsKey("password")) {
				request.setAttribute("err", "Booking details not found.");
				RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
				rd.forward(request, response);
				return;
			}

			System.out.println("Booking Details: " + bookingDetails);

			// Create request body as a list
			List<Map<String, Object>> requestBodyList = new ArrayList<>();
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("bookingId", booking_service_status_id.get("booking_id"));
			requestBody.put("amount", bookingDetails.get("price"));
			requestBody.put("currency", "SGD");
			requestBody.put("quantity", 1);
			requestBodyList.add(requestBody);

			// Set headers
			invocationBuilder.header("X-Username", bookingDetails.get("username"));
			invocationBuilder.header("X-Secret", bookingDetails.get("password"));
			invocationBuilder.header("X-third-party", "false"); // Ensure correct format

			// Send request with a list
			Response resp = invocationBuilder.post(Entity.entity(requestBodyList, MediaType.APPLICATION_JSON));
			System.out.println("Status: " + resp.getStatus());
			System.out.println("Response Body: " + resp.readEntity(String.class)); // Print response for debugging

			if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
				System.out.println("Success");
				Map<String, Object> responseMap = resp.readEntity(Map.class);
				if (responseMap != null && responseMap.containsKey("sessionUrl")) {
					String paymentUrl = (String) responseMap.get("sessionUrl");
					response.sendRedirect(paymentUrl);
				} else {
					throw new IOException("Invalid response from payment service.");
				}
			} else {
				System.out.println("Failed");
				request.setAttribute("err", "Payment processing failed.");
				RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
				rd.forward(request, response);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("err", "Database error: " + e.getMessage());
			RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
			rd.forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute("err", "Internal error: " + e.getMessage());
			RequestDispatcher rd = request.getRequestDispatcher("/pages/error.jsp");
			rd.forward(request, response);
		}
	}
}
