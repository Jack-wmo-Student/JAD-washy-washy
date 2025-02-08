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

public class TimeSlotDAO {
	public static List<Map<String, Object>> getTimeSlotSheets(String date, int serviceId) throws SQLException {
		System.out.println("We are in the model to get the small data");

		// Set the variables
		String query = """
				SELECT
					st.timeslot_id,
					st.created_at,
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
		List<Map<String, Object>> timeslot_sheets = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, serviceId);
			pstmt.setDate(2, java.sql.Date.valueOf(date));

			try (ResultSet rs = pstmt.executeQuery()) {

				while(rs.next()) {
					Map <String, Object> info = new HashMap<String, Object>();
					
					// Retrieve the timeslot_id from the result set
                    info.put("timeslot_id", rs.getInt("timeslot_id"));
                    info.put("created_at", rs.getDate("created_at"));
                    info.put("service_name", rs.getString("service_name"));
                    info.put("duration", rs.getInt("duration_in_hour"));
                    System.out.println("Retrieved Timeslot ID: " + info.get("timeslot_id"));
                    
                    timeslot_sheets.add(info);;
				}
				
				return timeslot_sheets;
			}
		} catch (Exception e) {
			System.out.println("------ Error in getTimeSlotSheets -------");
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
	
	public static List<Integer> getSpecificTimeslotsByIds (String time_slot, Integer[] time_slot_ids)  throws SQLException {
		// Define variables
//		Integer[] time_slots = new Integer[time_slot_ids.length];
		List<Integer> time_slots = new ArrayList<>();
		
		String query = """
			    SELECT 
			    	"%s"
				FROM 
					timeslot 
			    WHERE 
				   timeslot_id 
				IN 
					(%s)
			    """.formatted(
			        time_slot,
			        "?,".repeat(time_slot_ids.length).replaceAll(",$", ""));
		
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			// Set the '?'s
			for(int i = 0; i < time_slot_ids.length; i++) {
				pstmt.setInt(i + 1, time_slot_ids[i]);
			}
			
			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					time_slots.add(rs.getInt(1));
					System.out.println("User that booked this time slot: " + rs.getInt(1));
				}
				
				return time_slots;
			}
		} 
		catch (Exception e) {
			System.out.println("------ Error in getSpecificTimeslotsByIds -------");
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}		
	}
	
	
}















