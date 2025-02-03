package CONTROLLER;

import java.util.Map;

import MODEL.CLASS.Booking;
import MODEL.CLASS.TimeSlot;

import java.util.HashMap;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import utils.sessionUtils;

public class bookingPageLogic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String dbClass = System.getenv("DB_CLASS");
	private static final String dbUrl = System.getenv("DB_URL");
	private static final String dbPassword = System.getenv("DB_PASSWORD");
	private static final String dbUser = System.getenv("DB_USER");
	
	public bookingPageLogic() {
		super();	
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Declarations
		System.out.println("I am in booking Logic");
		
		// Check if the user is logged in or not
		HttpSession session = request.getSession(false);
		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session==null || session.getAttribute("userId") == null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}
		
		//Get the user Id 
		int user_id = (int) session.getAttribute("userId");
		
	  	List<Booking> bookingList = new ArrayList<>();
		
		try {  		
	  		// Fetch the first data that we need. Booking_id, date, service_name and timeslot_id
	  		List<Map<String, Object>> resultLists = fetchUserBookings(user_id); // Need to change to dynamic
		  	
//	  		Example result that we want to return to the front end
//	  		[
//	  		 	{
//	  		 		"booking_id": 123,
//	  		 		"booked_date": "2022-01-25",
//                  "service_name": "Wash and Wax",
//                  "timeslot_id": 3,
//                  "timeSlots": "8am-10am"
//	  		 	}
//	  		]
	  		
	  		// Fetch Data 3. Get the timeslots by using booking Ids
	  		for(int i = 0; i < resultLists.size(); i++) {
	  			// Declare stuffs
	  			TimeSlot timeslot = new TimeSlot();  			
	  			Map<String, Object> eachBooking = resultLists.get(i);
	  			int bookingId = (int) eachBooking.get("booking_id");
	  			int timeslotId = (int) eachBooking.get("timeslot_id");
	  			
	  			// Fetch the timeslots now
	  			List<String> timeslots = fetchBookedTimeSlots(bookingId, timeslotId);
	  			
	  			if(timeslots.size() != 1) {
	  				String[] firstPart = timeslots.get(0).split("-");
	  				String[] lastPart = timeslots.get(timeslots.size()-1).split("-");
	  				
	  				String finalisedTimeSlot = firstPart[0] + "-" + lastPart[1];
	  				
	  				// Create the timeslot class
	  				timeslot.setTimeSlotId(timeslotId);
	  				timeslot.setTimeRange(finalisedTimeSlot);
	  			}
	  			else {
	  				timeslot.setTimeSlotId(timeslotId);
	  				timeslot.setTimeRange((String) timeslots.get(0));
	  			}
	  			
	  			// Create the booking object and add to the list
	  			Booking bookingObj = new Booking(bookingId, (String) eachBooking.get("service_name"), timeslot, eachBooking.get("booked_date").toString());
	  			bookingList.add(bookingObj);
	  		}
	  		
		    // Set the session
		    session.setAttribute("bookingLists", bookingList);
		    
		    // Forward to the JSP
		    request.getRequestDispatcher("/pages/bookingPage.jsp").forward(request, response);
	      
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("I am in booking Post Logic");
		
		// Check if the user is logged in or not
		HttpSession session = request.getSession(false);
		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session==null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}
		
		// Set Class
		try {
			Class.forName(dbClass);
		} catch(ClassNotFoundException e) {
			System.out.printf("Connection drive issue.", e);
			e.printStackTrace();
		}
		
		// Get the data from front end. Specific date, service id, user id
		String date = request.getParameter("booking_date");
		String serviceId = request.getParameter("service_booked_id");
		
		
		// validate if the data is valid, if not, error, otherwise redirect to timeslot page
		if(date == null || date.trim().isEmpty() || serviceId == null || serviceId.trim().isEmpty()) {
			
			request.setAttribute("errorMessage", "Please choose a valid date or service!");
			
			response.sendRedirect(request.getContextPath() + "/pages/bookingPage.jsp");
		}
		else { // send them to the timeslot page already
			request.setAttribute("date", (String) date);
			request.setAttribute("serviceId", serviceId);
			HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
				@Override
				public String getMethod() {
					return "GET";
				}
			};

			
			request.getRequestDispatcher("/timeSlotLogic").forward(wrappedRequest, response);
		}
	}
		
	
	
	private static List<Map<String, Object>> fetchUserBookings(int userId) throws SQLException {
		// Set Class
		try {
			Class.forName(dbClass);
		} catch(ClassNotFoundException e) {
			System.out.printf("Connection drive issue.", e);
			e.printStackTrace();
		}
		
		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		
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
			                b.user_id = ?
			            ORDER BY
			 		    	b.booked_date DESC;
			        """;
		 
		 List<Map<String, Object>> resultList = new ArrayList<>();
		 try(PreparedStatement pstmt1 = conn.prepareStatement(query);) {
			 pstmt1.setInt(1, userId);
			 
			 try(ResultSet rs = pstmt1.executeQuery()) {
				 while(rs.next()) {
					 Map<String, Object> result = new HashMap<>();
                     result.put("booking_id", rs.getInt("booking_id"));
                     result.put("booked_date", rs.getDate("booked_date"));
                     result.put("service_name", rs.getString("service_name"));
                     result.put("timeslot_id", rs.getInt("timeslot_id"));
                     resultList.add(result);
				 }
			 }
		 }
		
		return resultList;
	}

	private static List<String> fetchBookedTimeSlots(int bookingId, int timeSlotId) throws SQLException {
		// Set Class
		try {
			Class.forName(dbClass);
		} catch(ClassNotFoundException e) {
			System.out.printf("Connection drive issue.", e);
			e.printStackTrace();
		}
				
		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		
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
		
		List<String> resultList = new ArrayList<>();
		try(PreparedStatement pstmt = conn.prepareStatement(query)) {
			for(int i = 1; i <= 9; i++) {
				pstmt.setInt(i, bookingId);
			}
			pstmt.setInt(10, timeSlotId);
			
			try(ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
	                String bookedTimeslot = rs.getString("booked_timeslot");
	                if (bookedTimeslot != null) {
	                	resultList.add(bookedTimeslot);
	                }
	            }
			}
		}
		
		return resultList;
	}

}
