package CONTROLLER;

import java.io.IOException;
import java.util.List;
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");

		if (user == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int userId = user.getUserId();
		List<Map<String, Integer>> bookingList = bookingDAO.getClosestFutureBookings(userId);

		// Store all bookings in request scope for UI rendering
		request.setAttribute("bookingList", bookingList);

		request.getRequestDispatcher("/pages/accountSettings.jsp").forward(request, response);
	}
}
