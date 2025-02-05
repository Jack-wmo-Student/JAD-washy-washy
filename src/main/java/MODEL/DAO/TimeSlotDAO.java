package MODEL.DAO;

import MODEL.CLASS.*;
import DBACCESS.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSlotDAO {
	public static Map<String, Object> getSmallInfoByServiceIdAndDate(String date, int serviceId) throws SQLException {
		System.out.println("We are in the model to get the small data");

		// Set the stuffs
		String query = """
				SELECT
					st.timeslot_id,
					s.service_name,
					s.duration_in_hour
				FROM
					service_timeslot st
				JOIN
					service s ON st.service_id = s.service_id
				WHERE
					st.service_id = ? AND
					st.service_timeslot_date = ?;
				""";
		Map<String, Object> info = new HashMap<>();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, serviceId);
			pstmt.setDate(2, java.sql.Date.valueOf(date));

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					// Retrieve the timeslot_id from the result set
					info.put("timeslot_id", rs.getInt("timeslot_id"));
					info.put("service_name", rs.getString("service_name"));
					info.put("duration", rs.getInt("duration_in_hour"));

					System.out.println("Retrieved Timeslot ID: " + info.get("timeslot_id"));
				} else {
					System.out.println("No matching timeslot found.");
				}
				return info;
			}
		} catch (Exception e) {
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}
	}

	public static List<Integer> getTimeslotsByTimeslotId(int timeslot_id) throws SQLException {
		System.out.println("We are in the function to get the timeslots ");

		// Define variables
		String[] slots = { "8am-9am", "9am-10am", "10am-11am", "11am-12pm", "1pm-2pm", "2pm-3pm", "3pm-4pm", "4pm-5pm",
				"5pm-6pm" };
		String query = """
				SELECT
					"8am-9am", "9am-10am", "10am-11am",
					"11am-12pm", "1pm-2pm", "2pm-3pm",
					"3pm-4pm", "4pm-5pm", "5pm-6pm"
				FROM
					timeslot
				WHERE
					timeslot_id = ?;
				""";
		List<Integer> timeslots = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, timeslot_id);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					// Loop the columns and add inside 'timeslots'
					for (String slot : slots) {
						timeslots.add(rs.getInt(slot)); // if it is null, will return 0
					}
				} else {
					System.out.println("No matching timeslot found.");
				}

				return timeslots;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}





