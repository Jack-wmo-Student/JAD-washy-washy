package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

import MODEL.DAO.BookingDAO;

/**
 * Servlet implementation class SetTrackedService
 */
public class SetTrackedService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BookingDAO bookingDAO = new BookingDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String bookingIdStr = request.getParameter("bookingId");
		if (bookingIdStr == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int bookingId = Integer.parseInt(bookingIdStr);
		Map<String, Integer> trackedService = bookingDAO.getBookingById(bookingId);

		if (trackedService != null) {
			// Store the tracked service in the session
			session.setAttribute("currentTrackedService", trackedService);

			// Redirect to AcknowledgeController after setting the session
			response.sendRedirect(request.getContextPath() + "/AcknowledgeServlet");
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
