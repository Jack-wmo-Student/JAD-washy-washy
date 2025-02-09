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
				    SELECT booking_id, service_id, status_id
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
					result.put("booking_Id", rs.getInt("booking_id"));
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

	public Map<String, Object> getBookingDetails(int serviceId, int bookingId) throws SQLException {
		// Query 1: Get timeslot details for the given service_id
		String query1 = "SELECT timeslot_id FROM booking WHERE service_id = ? AND booking_id = ?";

		// Query 2: Get service price for the given service_id
		String query2 = "SELECT price FROM service WHERE service_id = ?";

		// Query 3: Get taken_by_user_id from service_timeslot for the given service_id
		String query3 = "SELECT taken_by_user_id FROM service_timeslot WHERE service_id = ? AND timeslot_id = ?";

		// Query 4: Get user details using the taken_by_user_id
		String query4 = "SELECT username, password FROM users WHERE user_id = ?";

		Connection conn = null;
		PreparedStatement pstmt1 = null, pstmt2 = null, pstmt3 = null, pstmt4 = null;
		ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null;

		// Create a Map to store results
		Map<String, Object> resultMap = new HashMap<>();

		try {
			conn = DBConnection.getConnection(); // Assuming you have a DB connection utility

			// Query 1: Get timeslot_id
			pstmt1 = conn.prepareStatement(query1);
			pstmt1.setInt(1, serviceId);
			pstmt1.setInt(2, bookingId);
			rs1 = pstmt1.executeQuery();

			int timeslotId = -1;
			if (rs1.next()) {
				timeslotId = rs1.getInt("timeslot_id");
				resultMap.put("timeslot_id", timeslotId);
			}

			// Query 2: Get service price
			pstmt2 = conn.prepareStatement(query2);
			pstmt2.setInt(1, serviceId);
			rs2 = pstmt2.executeQuery();

			double price = 0.0;
			if (rs2.next()) {
				price = rs2.getDouble("price");
				resultMap.put("price", price);
			}

			// Query 3: Get taken_by_user_id
			pstmt3 = conn.prepareStatement(query3);
			pstmt3.setInt(1, serviceId);
			pstmt3.setInt(2, timeslotId);
			rs3 = pstmt3.executeQuery();

			int takenByUserId = -1;
			if (rs3.next()) {
				takenByUserId = rs3.getInt("taken_by_user_id");
				resultMap.put("taken_by_user_id", takenByUserId);
			}

			// Query 4: Get user details
			pstmt4 = conn.prepareStatement(query4);
			pstmt4.setInt(1, takenByUserId);
			rs4 = pstmt4.executeQuery();

			String username;
			String password = null;
			if (rs4.next()) {
				username = rs4.getString("user_id");
				password = rs4.getString("password");
				resultMap.put("username", username);
				resultMap.put("password", password);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources
			close(rs1, pstmt1);
			close(rs2, pstmt2);
			close(rs3, pstmt3);
			close(rs4, pstmt4);
			if (conn != null)
				conn.close();
		}

		return resultMap;
	}

	// Utility method to close resources
	private void close(ResultSet rs, PreparedStatement pstmt) {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
