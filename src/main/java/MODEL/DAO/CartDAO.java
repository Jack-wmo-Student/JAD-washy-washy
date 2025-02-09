package MODEL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DBACCESS.DBConnection;
import MODEL.CLASS.CartItem;

public class CartDAO {

	public List<Integer> processCartItems(int userId, List<CartItem> cartItems) throws SQLException {
		List<Integer> bookingIdLists = new ArrayList<>();

		try (Connection connection = DBConnection.getConnection()) {
			connection.setAutoCommit(false); // Begin transaction

			for (CartItem item : cartItems) {
				int bookingId = insertBooking(connection, userId, item);
				bookingIdLists.add(bookingId);
				updateTimeSlots(connection, item, bookingId);
			}

			connection.commit(); // Commit transaction
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("An error occurred while processing the booking.", e);
		}

		return bookingIdLists;
	}

	private int insertBooking(Connection connection, int userId, CartItem item) throws SQLException {
		String query = """
				    INSERT INTO booking (booked_by_user_id, timeslot_id, service_id, status_id, booked_date, feedback_id)
				    VALUES (?, ?, ?, ?, ?, NULL)
				    RETURNING booking_id
				""";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, item.getTimeslot().getTimeSlotId());
			stmt.setInt(3, item.getService().getId());
			stmt.setInt(4, 1); // Assuming status_id 1 is for booked.
			stmt.setDate(5, java.sql.Date.valueOf(item.getBookedDate()));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("Successful checkout!");
					return rs.getInt("booking_id");
				} else {
					throw new SQLException("Failed to retrieve booking_id.");
				}
			}
		}
	}

	private void updateTimeSlots(Connection connection, CartItem item, int bookingId) throws SQLException {
		int duration = item.getService().getDurationInHour();
		String[] timeRanges = { "8am-9am", "9am-10am", "10am-11am", "11am-12pm", "1pm-2pm", "2pm-3pm", "3pm-4pm",
				"4pm-5pm", "5pm-6pm" };
		String selectedTimeRange = item.getTimeslot().getTimeRange();
		String formattedTimeRange = getStartTimeSlot(selectedTimeRange);
		int startIndex = Arrays.asList(timeRanges).indexOf(formattedTimeRange);

		if (startIndex == -1 || startIndex + duration > timeRanges.length) {
			throw new SQLException("Invalid time range or duration exceeds available slots.");
		}

		for (int i = startIndex; i < startIndex + duration; i++) {
			String updateQuery = "UPDATE timeslot SET \"" + timeRanges[i] + "\" = ? WHERE timeslot_id = ?";

			try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
				stmt.setInt(1, bookingId);
				stmt.setInt(2, item.getTimeslot().getTimeSlotId());
				stmt.executeUpdate();
			}
		}
	}
	
	private String getStartTimeSlot (String input_time_slot) {
		String startTime = input_time_slot.split("-")[0];
		
		String[] all_time_slots= {
				"8am-9am", "9am-10am", "10am-11am", "11am-12pm", 
				"1pm-2pm", "2pm-3pm", "3pm-4pm", "4pm-5pm", "5pm-6pm"
		};
		
		for (String slot : all_time_slots) {
	        if (slot.startsWith(startTime)) {
	            return slot;
	        }
	    }
	    return null; 
	}
}
