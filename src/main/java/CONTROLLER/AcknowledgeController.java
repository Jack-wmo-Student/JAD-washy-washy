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
import java.util.HashMap;
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
		Map<String, Integer> booking_service_status_id = (Map<String, Integer>) session
				.getAttribute("currentTrackedService");

		BookingDAO bookingDAO = new BookingDAO();
		Map<String, Object> bookingDetails;

		try {
			bookingDetails = bookingDAO.getBookingDetails(booking_service_status_id.get("service_id"),
					booking_service_status_id.get("booking_id"));
			// Create request body
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("bookingId", booking_service_status_id.get("booking_id"));
			requestBody.put("amount", bookingDetails.get("price"));
			requestBody.put("currency", "SGD");
			requestBody.put("quantity", 1);

			// Set headers
			String businessId = request.getHeader("X-Username");
			String secret = request.getHeader("X-Secret");
			String thirdParty = request.getHeader("X-third-party");

			invocationBuilder.header("X-Username", bookingDetails.get("username"));
			invocationBuilder.header("X-Secret", bookingDetails.get("password"));
			invocationBuilder.header("X-third-party", false);

			Response resp = invocationBuilder.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));
			System.out.println("status: " + resp.getStatus());

			if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
				System.out.println("success");
				Map<String, Object> responseMap = (Map<String, Object>) resp.readEntity(Map.class);
				String paymentUrl = (String) responseMap.get("sessionUrl");

				response.sendRedirect(paymentUrl);
			} else {
				System.out.println("failed");
				request.setAttribute("err", "NotFound");
				String url = request.getContextPath() + "/pages/error.jsp";
				RequestDispatcher rd = request.getRequestDispatcher(url);
				rd.forward(request, response);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
