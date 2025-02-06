package CONTROLLER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.Booking;
import MODEL.CLASS.TimeSlot;
import MODEL.DAO.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class BookingController extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	public BookingController() {
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
	  		List<Map<String, Object>> resultLists = BookingDAO.fetchUserBookings(user_id); // Need to change to dynamic
		  	
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
	  			List<String> timeslots = BookingDAO.fetchBookedTimeSlots(bookingId, timeslotId);
	  			
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

}
