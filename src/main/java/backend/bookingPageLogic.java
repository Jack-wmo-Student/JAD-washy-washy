package backend;

import jakarta.servlet.RequestDispatcher;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class bookingPageLogic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final String dbClass = System.getenv("DB_CLASS");
	private final String dbUrl = System.getenv("DB_URL");
	private final String dbPassword = System.getenv("DB_PASSWORD");
	private final String dbUser = System.getenv("DB_USER");
	
	public bookingPageLogic() {
		super();	
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResultSet result1 = null;
	  	Connection conn = null;
	  	
		
		// Set Class
		try {
			Class.forName(dbClass);
		} catch(ClassNotFoundException e) {
			System.out.printf("Connection drive issue.", e);
			e.printStackTrace();
		}
		
		try {
	  		// === Connect to Database ===
	  		conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	  		
	  		PreparedStatement pstmt1 = conn.prepareStatement(sql1);
	  		
	  		pstmt1.setInt(1, userId);
		  	
	  		// Fetch Data 3. Get the timeslots by using booking Ids
	  		while(result1.next()) {
	  			int bookingId = result1.getInt("booking_id");
	  			String sql2 = "SELECT \r\n"
		  					+ "    t.timeslot_id,\r\n"
		  					+ "    CASE \r\n"
		  					+ "        WHEN t.\"8am-9am\" = BOOKING_ID THEN '8am-9am'\r\n"
		  					+ "        WHEN t.\"9am-10am\" = BOOKING_ID THEN '9am-10am'\r\n"
		  					+ "        WHEN t.\"10am-11am\" = BOOKING_ID THEN '10am-11am'\r\n"
		  					+ "        WHEN t.\"11am-12pm\" = BOOKING_ID THEN '11am-12pm'\r\n"
		  					+ "        WHEN t.\"1pm-2pm\" = BOOKING_ID THEN '1pm-2pm'\r\n"
		  					+ "        WHEN t.\"2pm-3pm\" = BOOKING_ID THEN '2pm-3pm'\r\n"
		  					+ "        WHEN t.\"3pm-4pm\" = BOOKING_ID THEN '3pm-4pm'\r\n"
		  					+ "        WHEN t.\"4pm-5pm\" = BOOKING_ID THEN '4pm-5pm'\r\n"
		  					+ "        WHEN t.\"5pm-6pm\" = BOOKING_ID THEN '5pm-6pm'\r\n"
		  					+ "        ELSE NULL\r\n"
		  					+ "    END AS booked_timeslot\r\n"
		  					+ "FROM \r\n"
		  					+ "    timeslot t\r\n"
		  					+ "WHERE \r\n"
		  					+ "    t.timeslot_id = TIMESLOT_ID\r\n"
		  					+ "      AND BOOKING_ID IN (\r\n"
		  					+ "          t.\"8am-9am\", t.\"9am-10am\", t.\"10am-11am\",\r\n"
		  					+ "          t.\"11am-12pm\", t.\"1pm-2pm\", t.\"2pm-3pm\",\r\n"
		  					+ "          t.\"3pm-4pm\", t.\"4pm-5pm\", t.\"5pm-6pm\"\r\n"
		  					+ "      );";
	  		}
		  	
		  	
	  		
	  		
	  		
		    // Convert ResultSet to a list or array
	      	List<Map<String, String>> questionList = new ArrayList<>();
		    while (rs.next()) {
		    	Map<String, String> question = new HashMap<>();
		        question.put("question_id", rs.getString("question_id"));
		        question.put("question_text", rs.getString("question_text"));
		        question.put("question_type", rs.getString("question_type"));
		        questionList.add(question);
		    }
	      	
		    // Store the list in the request attribute
		    request.setAttribute("questions", questionList);

		    // Forward to the JSP
		    RequestDispatcher dispatcher = request.getRequestDispatcher("/feedbackPage.jsp");
		    dispatcher.forward(request, response);
	      
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static List<Map<String, Object>> fetchUserBookings(Connection conn, int userId) throws SQLException {
		 String query = """
		            SELECT 
		                b.booking_id,
		                b.date,
		                s.service_name,
		                b.timeslot_id
		            FROM 
		                booking b
		            JOIN 
		                service s ON b.service_id = s.service_id
		            WHERE 
		                b.user_id = ?;
		        """;
		 
		 List<Map<String, Object>> resultList = new ArrayList<>();
		 try(PreparedStatement pstmt1 = conn.prepareStatement(query);) {
			 pstmt1.setInt(1, userId);
			 
			 try(ResultSet rs = pstmt1.executeQuery()) {
				 while(rs.next()) {
					 Map<String, Object> result = new HashMap<>();
                     result.put("booking_id", rs.getInt("booking_id"));
                     result.put("date", rs.getDate("date"));
                     result.put("service_name", rs.getString("service_name"));
                     result.put("timeslot_id", rs.getInt("timeslot_id"));
                     resultList.add(result);
				 }
			 }
		 }
		
		return resultList;
	}
}









































