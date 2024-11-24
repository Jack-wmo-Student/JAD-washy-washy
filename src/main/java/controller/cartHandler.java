package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.cartItem;
import model.timeslot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Servlet implementation class cartHandler
 */
public class cartHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public cartHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve session
		HttpSession session = request.getSession(false);

		List<Integer> bookingIdLists = new ArrayList<>();
		
		if(session.getAttribute("bookingIdLists") == null) {
			// Create the list of booking id, add in the session
			session.setAttribute("bookingIdLists", bookingIdLists);
		}
		
		
		// Check if the user is logged in and session exists
		if (session == null || session.getAttribute("cart-item-list") == null) {
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
			return;
		}

		// Check if the userId exists in the session
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			response.sendRedirect(
					request.getContextPath() + "/pages/login.jsp?error=Invalid session. Please log in again.");
			return;
		}
		
		// Retrieve the cart items and user ID from the session
		@SuppressWarnings("unchecked")
		List<cartItem> cartItems = (List<cartItem>) session.getAttribute("cart-item-list");
	    if (cartItems == null) {
	        response.sendRedirect(request.getContextPath() + "/pages/bookingPage.jsp?error=Invalid session. Please log in again.");
	        return;
	    }

		// Database connection variables
		String DB_CLASS = System.getenv("DB_CLASS");
		String DB_URL = System.getenv("DB_URL");
		String DB_USER = System.getenv("DB_USER");
		String DB_PASSWORD = System.getenv("DB_PASSWORD");

		try {
			// Load the database driver
			Class.forName(DB_CLASS);

			try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
				connection.setAutoCommit(false); // Begin transaction

				for (cartItem item : cartItems) {
					// Step 1: Insert into the booking table
					String bookingQuery = """
							    INSERT INTO booking (user_id, timeslot_id, service_id, created_at, booked_date)
							    VALUES (?, ?, ?, NOW(), ?)
							    RETURNING booking_id
							""";

					int bookingId;
					try (PreparedStatement bookingStmt = connection.prepareStatement(bookingQuery)) {
						bookingStmt.setInt(1, userId);
						bookingStmt.setInt(2, item.getTimeslot().getTimeSlotId());
						bookingStmt.setInt(3, item.getService().getId());
						bookingStmt.setDate(4, java.sql.Date.valueOf(item.getBookedDate()));

						try (ResultSet rs = bookingStmt.executeQuery()) {
							if (rs.next()) {
								bookingId = rs.getInt("booking_id");
								bookingIdLists.add(bookingId);
							} else {
								throw new Exception("Failed to retrieve booking_id.");
							}
						}
						
						session.setAttribute("bookingIdLists", bookingIdLists);
					}

					// Step 2: Update the timeslot table with the booking_id
					String timeslotUpdateQuery = """
							    UPDATE timeslot
							    SET "8am-9am" = CASE WHEN ? = '8am-9am' THEN ? ELSE "8am-9am" END,
							        "9am-10am" = CASE WHEN ? = '9am-10am' THEN ? ELSE "9am-10am" END,
							        "10am-11am" = CASE WHEN ? = '10am-11am' THEN ? ELSE "10am-11am" END,
							        "11am-12pm" = CASE WHEN ? = '11am-12pm' THEN ? ELSE "11am-12pm" END,
							        "1pm-2pm" = CASE WHEN ? = '1pm-2pm' THEN ? ELSE "1pm-2pm" END,
							        "2pm-3pm" = CASE WHEN ? = '2pm-3pm' THEN ? ELSE "2pm-3pm" END,
							        "3pm-4pm" = CASE WHEN ? = '3pm-4pm' THEN ? ELSE "3pm-4pm" END,
							        "4pm-5pm" = CASE WHEN ? = '4pm-5pm' THEN ? ELSE "4pm-5pm" END,
							        "5pm-6pm" = CASE WHEN ? = '5pm-6pm' THEN ? ELSE "5pm-6pm" END
							    WHERE timeslot_id = ?
							""";

					try (PreparedStatement timeslotStmt = connection.prepareStatement(timeslotUpdateQuery)) {
						String timeRange = item.getTimeslot().getTimeRange();
						for (int i = 1; i <= 18; i += 2) {
							timeslotStmt.setString(i, timeRange);
							timeslotStmt.setInt(i + 1, bookingId);
						}
						timeslotStmt.setInt(19, item.getTimeslot().getTimeSlotId());
						timeslotStmt.executeUpdate();
					}
				}

				connection.commit(); // Commit transaction
				
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("error", "An error occurred while processing your booking. Please try again.");
				request.getRequestDispatcher("/pages/cart.jsp").forward(request, response);
				return;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			request.setAttribute("error", "Database connection error. Please contact support.");
			request.getRequestDispatcher("/pages/cart.jsp").forward(request, response);
			return;
		}

		// Reset the cart session attribute
		session.removeAttribute("cart-item-list");

		// Redirect to the booking confirmation page
		response.sendRedirect(request.getContextPath() + "/feedbackLogic");
	}

}
