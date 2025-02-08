package CONTROLLER;

import java.io.IOException;
import java.sql.Date;
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
			List<Map<String, Object>> time_slot_details = TimeSlotDAO.getTimeSlotSheets(date, serviceId);

			time_slot_details.sort((o1, o2) -> {
			    Date date1 = (Date) o1.get("created_at");
			    Date date2 = (Date) o2.get("created_at");
			    return date1.compareTo(date2);
			});
			
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
			 
			
			// Loop through to send to the front end
			for (int i = 0; i < all_time_slots.length - service_duration + 1; i += service_duration) {
			    String start_time = all_time_slots[i];
			    if (i + service_duration - 1 >= all_time_slots.length) {
			        break;
			    }
			    String end_time = all_time_slots[i + service_duration - 1];

			    // Skip slots that cross lunch break (12pm-1pm)
			    boolean crossesLunchBreak = false;
			    for (int j = i; j <= i + service_duration - 1; j++) {
			        if (all_time_slots[j].split("-")[0].contains("12pm")) {
			            crossesLunchBreak = true;
			            break;
			        }
			    }
			    
			    if (!crossesLunchBreak) {
			        time_slot_map.put(start_time, new Integer[time_slot_details.size()]);
			        System.out.println("Added slot to map: " + start_time);
			    }
			    
			    if (crossesLunchBreak) {
			        continue;
			    }

			    Integer[] availability_array = time_slot_map.get(start_time);

			    if (availability_array != null) {
			        try {
			            List<Integer> specific_time_slots = TimeSlotDAO.getSpecificTimeslotsByIds(start_time, time_slot_ids);
			            
			            
//			            System.out.println("Successful model" + specific_time_slots);
			            // Reset availability array
			            Arrays.fill(availability_array, 0);  // Default to available

			            // Update availability array based on specific time slots
			            System.out.println(specific_time_slots.size());
			            for (int j = 0; j < specific_time_slots.size(); j++) {
			                if (specific_time_slots.get(j) != null) {
			                    availability_array[j] = 1;  // Mark as booked
			                }
			            }

			            String time_range = start_time.split("-")[0] + "-" + end_time.split("-")[1];

			            // Check if the time slot is available
			            boolean is_available = true;
			            if (availability_array.length == 4) {  // Check if array has 4 items
			                if (availability_array[3] == 1) {  // Check if last item is 1
			                    is_available = false;
			                }
			            }

			            if (is_available) {
			                // Find the highest available timeslot_id (the rightmost 0)
			                int availableTimeslotId = -1;
			                for (int k = availability_array.length - 1; k >= 0; k--) {
			                    if (availability_array[k] == 0) {
			                        // Adding 1 because your timeslot_ids are 1-based (1 to 4)
			                        availableTimeslotId = k + 1;
			                        break;
			                    }
			                }
			                if (availableTimeslotId != -1) {
			                    to_send_front_end.put(time_range, String.valueOf(availableTimeslotId));
			                }
			            } else {
			                to_send_front_end.put(time_range, "unavailable");
			            }
			            

			        } catch (SQLException e) {
			            System.out.println("Error checking availability for time slot: " + start_time);
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
		String combined_value = request.getParameter("timeslot");
		String[] time_slot_data = combined_value.split(",");
		String chosen_time_slot = time_slot_data[0]; 	 				// e.g., "8am-9am"
		int time_slot_id = Integer.valueOf(time_slot_data[1]);			// e.g., 4
		
		
		// Retrieve the booking date from the session
		String bookingDate = (String) session.getAttribute("booking-date");

		// Retrieve other required attributes from the session
		@SuppressWarnings("unchecked")
		Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
				.getAttribute("categoryServiceMap");
		int serviceId = (int) session.getAttribute("service-id");

		// Retrieve cart from session or create a new one if it doesn't exist
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemLists = (List<CartItem>) session.getAttribute("cart-item-list");
		if (cartItemLists == null) {
			cartItemLists = new ArrayList<>();
		}

		// Create cartItemObj
		CartItem cartItemObj = new CartItem();

		// Create timeslot obj
		TimeSlot timeslotObj = new TimeSlot(time_slot_id, chosen_time_slot);

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
