package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import MODEL.CLASS.CartItem;
import MODEL.CLASS.Category;
import MODEL.CLASS.Service;
import MODEL.CLASS.TimeSlot;
import MODEL.DAO.TimeSlotDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class TimeSlotController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public TimeSlotController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Check if the user is logged in or not
		HttpSession session = request.getSession(false);
		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session==null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}
		
		System.out.println("We are in doGet of the timeslotPage");

		// Get data from the params and define variables
		String date = (String) request.getAttribute("date");
		String strServiceId = (String) request.getAttribute("serviceId");
		int serviceId = 0;
		String[] all_time_slots= {
				"8am-9am", "9am-10am", "10am-11am", "11am-12pm", 
				"1pm-2pm", "2pm-3pm", "4pm-5pm", "5pm-6pm"
		};
		
		
		
		
		// Confirm correct date and service id
		System.out.println("--- Chosen Date: " + date);
		System.out.println("--- Chosen serviceId: " + strServiceId);

		if (date != null && strServiceId != null) {
			try {
				serviceId = Integer.parseInt(strServiceId);
			} catch (NumberFormatException e) {
				System.out.printf("Failed to parse to integer. ", e);
				response.sendRedirect(
						request.getContextPath() + "/pages/bookingPage.jsp?errorMessage=Invalid service ID");
			}
		} else {
			System.out.println("Did not have the params");
			response.sendRedirect(request.getContextPath()
					+ "/pages/bookingPage.jsp?errorMessage=Does not have the necessary params");
			;
		}

		try {
			// Get the timeslot_id, created_at, duration_in_hour and price from 'service_timeslot' using
			// service id and date
			List<Map<String, Object>> time_slot_details= TimeSlotDAO.getTimeSlotSheets(date, serviceId);

			// Get all the time slots from the 'timeslot'
			if (time_slot_details == null || time_slot_details.isEmpty()) {
				response.sendRedirect(
						request.getContextPath() + "/pages/bookingPage.jsp?errorMessage=Invalid service ID or date");
				return;
			}

			// Here, I see how long the duration of the service is.
			int service_duration = (int) time_slot_details.get(0).get("duration"); // 1, 2, 3
			
//			Based on the service duration, I need to create a map array to keep track of which timeslots are already occupied already.
			Map<String, Integer[]> time_slot_map = new LinkedHashMap<>();
			// Example data
			// {
			//	"8am-9am": [null, null, null, null],
			//	"10am-11am": [null, null, null, null],
			// }
//			Create a map to send to front end
			Map<String, String> to_send_front_end = new LinkedHashMap<>();
			// Example Data
			// {
            //    "8am-10am": ["Booked", "Booked", "Booked", "Booked"],
            //    "10am-12pm": ["Booked", "Booked", "Booked", "Available"],
			//	  "1pm-2pm": ["Booked", "Booked", "Available", "Available"] 
            // }
			
			int total_slots = all_time_slots.length;
			
			for(int i = 0; i < total_slots; i++) {
				String start_slot = all_time_slots[i];
				int end_index = i + service_duration - 1;
				
				// Ensure the end index is within the bounds
				if (end_index < total_slots) {
					String end_slot = all_time_slots[end_index];
					
					// Check if the time slot crosses 12pm - 1pm (lunch break)
					if(!start_slot.contains("11am-12pm") || end_slot.contains("12pm-1pm")) {
						time_slot_map.put(start_slot, new Integer[time_slot_details.size()]);
					} 
				}	
			}
			
			// Make a list of time slot ids that i will need to pass in
			Integer[] time_slot_ids = new Integer[time_slot_details.size()];
			for (int i = 0; i < time_slot_details.size(); i++) {
					time_slot_ids[i] = (int) time_slot_details.get(i).get("timeslot_id");
			}
			 
			
			
			// Now, need to fetch from the database to see which timeslots are empty and which are available.
			// This will then fill the time_slot_Map
//			time_slot_map.forEach((start_time, availability_array) -> {
//				Integer[] specific_time_slots = null;
//				try {
//					specific_time_slots = TimeSlotDAO.getSpecificTimeslotsByIds(start_time, time_slot_ids);
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				for (int i = 0; i < specific_time_slots.length; i++) {
//					if(specific_time_slots[i] == null) {
//						availability_array[i] = 0; // Available
//					}
//					else {
//						availability_array[i] = 1; // Booked
//					}
//				}
//				
//				// Create frontend time range and check availability
//				int start_index = Arrays.asList(all_time_slots).indexOf(start_time);
//				String end_time = all_time_slots[start_index + service_duration - 1];
//			    String time_range = start_time.split("-")[0] + "-" + end_time.split("-")[1];
//				
//				// Check availability
//			    boolean is_available = false;
//			    if(availability_array.length == 4) {
//			    	is_available = availability_array[3] != 1;
//			    }	else is_available = true; 
//			    
//			    
//			    to_send_front_end.put(time_range, is_available ? "available" : "unavailable");
//			});

			for (int i = 0; i < all_time_slots.length - service_duration + 1; i++) {
			    String start_time = all_time_slots[i];
			    String end_time = all_time_slots[i + service_duration - 1];
			    
			    // Only skip if the start time is 12pm
			    if (start_time.contains("12pm")) {
			        continue;
			    }
			    
			    Integer[] availability_array = time_slot_map.get(start_time);
			    
			    if (availability_array != null) {
			        Integer[] specific_time_slots = null;
			        try {
			            specific_time_slots = TimeSlotDAO.getSpecificTimeslotsByIds(start_time, time_slot_ids);
			            
			            // Update availability array
			            for (int j = 0; j < specific_time_slots.length; j++) {
			                if(specific_time_slots[j] == null) {
			                    availability_array[j] = 0;
			                } else {
			                    availability_array[j] = 1;
			                }
			            }
			            
			            String time_range = start_time.split("-")[0] + "-" + end_time.split("-")[1];
			            
			            boolean is_available = false;
			            if(availability_array.length == 4) {
			                is_available = availability_array[3] != 1;
			            } else {
			                is_available = true;
			            }
			            
			            to_send_front_end.put(time_range, is_available ? "available" : "unavailable");
			            
			        } catch (SQLException e) {
			            e.printStackTrace();
			            continue;
			        }
			    }
			}
			
			
			// Store the list in the session attribute
			session.setAttribute("timeslot-availability", to_send_front_end);
			session.setAttribute("service-id", serviceId);
			session.setAttribute("booking-date", date);

			// Create the session for the list of carts
			// Retrieve the cart list from the session, or create a new one if it doesn't
			// exist
			@SuppressWarnings("unchecked")
			List<CartItem> cartItemLists = (List<CartItem>) session.getAttribute("cart-item-list");
			if (cartItemLists == null) {
				cartItemLists = new ArrayList<>(); // Initialize a new list if it's null
				session.setAttribute("cart-item-list", cartItemLists); // Store it back in the session
			}

			response.sendRedirect(request.getContextPath() + "/pages/timeSlotPage.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		// Check if the user is logged in
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null) {
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}
		// Get the data
		String chosenTimeRange = request.getParameter("timeslot");

		// get data from the session
		// Retrieve the booking date from the session
		@SuppressWarnings("unchecked")
		Map<String, Object> timeslotAvailability = (Map<String, Object>) session.getAttribute("timeslot-availability");
		String bookingDate = (String) timeslotAvailability.get("booking_date");

		// Retrieve other required attributes from the session
		@SuppressWarnings("unchecked")
		Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
				.getAttribute("categoryServiceMap");
		int serviceId = (int) session.getAttribute("service-id");
		int timeslotId = (int) session.getAttribute("timeslot-id");

		// Retrieve cart from session or create a new one if it doesn't exist
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemLists = (List<CartItem>) session.getAttribute("cart-item-list");
		if (cartItemLists == null) {
			cartItemLists = new ArrayList<>();
		}

		// Create cartItemObj
		CartItem cartItemObj = new CartItem();

		// Create timeslot obj
		TimeSlot timeslotObj = new TimeSlot(timeslotId, chosenTimeRange);

		// loop to get the service object
		Map<String, Object> serviceDetails = new HashMap<>();

		for (Map.Entry<Category, List<Service>> entry : sessionCategoryServiceMap.entrySet()) {
			List<Service> services = entry.getValue();

			for (Service srv : services) {
				if (srv.getId() == serviceId) {
					// if we arrive here, it means the service is correct now
					// set everything inside the cartItem
					cartItemObj.setBookedDate(bookingDate);
					cartItemObj.setTimeslot(timeslotObj);
					cartItemObj.setService(srv);
				}
			}
		}

		// Push to cartItemList session thing
		cartItemLists.add(cartItemObj);

		// Store the list in the session attribute again to reflect the changes
		session.setAttribute("cart-item-list", cartItemLists);

		// Redirect to the cart page
		response.sendRedirect(request.getContextPath() + "/pages/cart.jsp");

	}

	// --- Functions ----
	private List<Integer> formatTimeslots(List<Integer> timeslots, int duration) {
		// Declarations
		List<Boolean> bool_timeslots = new ArrayList<>();
		List<Integer> formattedTimeslots = new ArrayList<>();

		// Change it to boolean list to make it easier to check along the way

		for (int i = 0; i < timeslots.size(); i++) {
			if (timeslots.get(i) == 0) {
				bool_timeslots.add(true);
			} else {
				bool_timeslots.add(false);
			}
		}

		// Start the process
		for (int i = 0; i < bool_timeslots.size(); i++) {
			if (!bool_timeslots.get(i)) {
				// If the current timeslot is false, mark as 0
				formattedTimeslots.add(0);
			} else {
				// Check the next `duration` slots
				boolean allTrue = true;
				for (int j = i; j < i + duration && j < timeslots.size(); j++) {
					if (!bool_timeslots.get(j)) {
						allTrue = false;
						break;
					}
				}

				if (i + duration <= timeslots.size() && allTrue) {
					// If we have enough slots and all are true
					formattedTimeslots.add(2);
				} else {
					// Not enough slots or a false was encountered
					formattedTimeslots.add(1);
				}
			}
		}

		return formattedTimeslots;
	}

}
