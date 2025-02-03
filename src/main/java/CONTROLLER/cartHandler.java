package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import MODEL.CLASS.CartItem;

import java.util.ArrayList;

public class cartHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public cartHandler() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		List<Integer> bookingIdLists = new ArrayList<>();
		
		// Check if the user is logged in and session exists
		if (session == null || session.getAttribute("cart-item-list") == null) {
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
			return;
		}
		
		if(session.getAttribute("bookingIdLists") == null) {
			// Create the list of booking id, add in the session
			session.setAttribute("bookingIdLists", bookingIdLists);
		}
		

		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			response.sendRedirect(
					request.getContextPath() + "/pages/login.jsp?error=Invalid session. Please log in again.");
			return;
		}

		@SuppressWarnings("unchecked")
		List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart-item-list");
		if (cartItems == null) {
			response.sendRedirect(
					request.getContextPath() + "/pages/bookingPage.jsp?error=Invalid session. Please log in again.");
			return;
		}

		String DB_CLASS = System.getenv("DB_CLASS");
		String DB_URL = System.getenv("DB_URL");
		String DB_USER = System.getenv("DB_USER");
		String DB_PASSWORD = System.getenv("DB_PASSWORD");

		try {
			Class.forName(DB_CLASS);

			try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
				connection.setAutoCommit(false); // Begin transaction

				for (CartItem item : cartItems) {
					// Step 1: Insert into the booking table
					String bookingQuery = """
							    INSERT INTO booking (user_id, timeslot_id, service_id, feedback_id, created_at, booked_date)
							    VALUES (?, ?, ?, null, NOW(), ?)
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

					// Step 2: Calculate the time slots to update based on service duration
					int duration = item.getService().getDurationInHour();
					String[] timeRanges = { "8am-9am", "9am-10am", "10am-11am", "11am-12pm", "1pm-2pm", "2pm-3pm",
							"3pm-4pm", "4pm-5pm", "5pm-6pm" };
					String selectedTimeRange = item.getTimeslot().getTimeRange();
					int startIndex = Arrays.asList(timeRanges).indexOf(selectedTimeRange);

					if (startIndex == -1 || startIndex + duration > timeRanges.length) {
						throw new Exception("Invalid time range or duration exceeds available slots.");
					}

					// Step 3: Update the time slots with booking_id
					for (int i = startIndex; i < startIndex + duration; i++) {
						String timeslotUpdateQuery = "UPDATE timeslot SET \"" + timeRanges[i]
								+ "\" = ? WHERE timeslot_id = ?";

						try (PreparedStatement timeslotStmt = connection.prepareStatement(timeslotUpdateQuery)) {
							timeslotStmt.setInt(1, bookingId);
							timeslotStmt.setInt(2, item.getTimeslot().getTimeSlotId());
							timeslotStmt.executeUpdate();
						}
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
