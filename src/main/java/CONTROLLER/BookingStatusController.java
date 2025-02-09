package CONTROLLER;

import java.io.IOException;
import java.util.Map;

import MODEL.CLASS.Booking;
import MODEL.CLASS.User;
import MODEL.DAO.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class BookingStatusController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BookingDAO bookingDAO;

	public BookingStatusController() {
		this.bookingDAO = new BookingDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		if (user == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		int userId = user.getUserId();

		Map<String, Integer> booking_service_status_id = bookingDAO.getClosestFutureBooking(userId);
		if (booking_service_status_id != null) {
			// Store serviceId in request scope so included JSPs can access it
			int statusId = booking_service_status_id.get("status_id");
			request.setAttribute("statusId", statusId);
			session.setAttribute("currentTrackedService", booking_service_status_id);
		}

		// Forward to the main JSP that includes bookingProgress.jsp
		request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
	}
}
