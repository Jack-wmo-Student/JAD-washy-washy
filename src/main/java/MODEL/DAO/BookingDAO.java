package MODEL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DBACCESS.DBConnection;
import MODEL.CLASS.Booking;

public class BookingDAO {
	public static List<Map<String, Object>> fetchUserBookings(int userId) throws SQLException {
		// Define the stuffs
		List<Map<String, Object>> resultList = new ArrayList<>();

		String query = """
				    SELECT
				        b.booking_id,
				        b.booked_date,
				        s.service_name,
				        b.timeslot_id
				    FROM
				        booking b
				    JOIN
				        service s ON b.service_id = s.service_id
				    WHERE
				        b.booked_by_user_id = ?
				    ORDER BY
				b.booked_date DESC;
				""";

		// Use try-with-resources to ensure connection is closed properly
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt1 = conn.prepareStatement(query)) {

			pstmt1.setInt(1, userId);

			try (ResultSet rs = pstmt1.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> result = new HashMap<>();
					result.put("booking_id", rs.getInt("booking_id"));
					result.put("booked_date", rs.getDate("booked_date"));
					result.put("service_name", rs.getString("service_name"));
					result.put("timeslot_id", rs.getInt("timeslot_id"));
					resultList.add(result);
				}
			}

		} catch (SQLException e) {
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e; // Rethrow to inform the caller about the failure
		}

		return resultList;
	}

	public static List<String> fetchBookedTimeSlots(int bookingId, int timeSlotId) throws SQLException {
		// Initialize stuffs
		List<String> resultList = new ArrayList<>();
		System.out.println("----- we are in fetchBookedTimeSlots ------");

		String query = """
				     SELECT
				        t.timeslot_id,
				        CASE
				            WHEN t."8am-9am" = ? THEN '8am-9am'
				            WHEN t."9am-10am" = ? THEN '9am-10am'
				            WHEN t."10am-11am" = ? THEN '10am-11am'
				            WHEN t."11am-12pm" = ? THEN '11am-12pm'
				            WHEN t."1pm-2pm" = ? THEN '1pm-2pm'
				            WHEN t."2pm-3pm" = ? THEN '2pm-3pm'
				            WHEN t."3pm-4pm" = ? THEN '3pm-4pm'
				            WHEN t."4pm-5pm" = ? THEN '4pm-5pm'
				            WHEN t."5pm-6pm" = ? THEN '5pm-6pm'
				            ELSE NULL
				        END AS booked_timeslot
				    FROM
				        timeslot t
				    WHERE
				        t.timeslot_id = ?;
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			// Set parameters for the query
			for (int i = 1; i <= 9; i++) {
				pstmt.setInt(i, bookingId);
			}
			pstmt.setInt(10, timeSlotId);

			try (ResultSet rs = pstmt.executeQuery()) {
				System.out.println("----- Successful query ------");
				while (rs.next()) {
					String bookedTimeslot = rs.getString("booked_timeslot");
					if (bookedTimeslot != null) {
						resultList.add(bookedTimeslot);
					}
				}
			}

		} catch (SQLException e) {
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e; // Rethrow the exception so the caller is aware of the failure
		}

		return resultList;
	}

	public Map<String, Integer> getClosestFutureBooking(int userId) {
		String query = """
				    SELECT service_id, status_id
				    FROM booking
				    WHERE booked_by_user_id = ? AND booked_date > NOW()
				    ORDER BY booked_date ASC
				    LIMIT 1
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Return a map containing service_id and status_id
					Map<String, Integer> result = new HashMap<>();
					result.put("service_id", rs.getInt("service_id"));
					result.put("status_id", rs.getInt("status_id"));
					return result;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null; // Return null if no future bookings are found
	}
}
