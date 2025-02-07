package CONTROLLER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

		// Get data from the params
		String date = (String) request.getAttribute("date");
		String strServiceId = (String) request.getAttribute("serviceId");
		int serviceId = 0;
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
			Map<String, Object> smallInfo = TimeSlotDAO.getTimeSlotSheets(date, serviceId);

			// Get all the time slots from the 'timeslot'
			if (smallInfo == null || smallInfo.isEmpty()) {
				response.sendRedirect(
						request.getContextPath() + "/pages/bookingPage.jsp?errorMessage=Invalid service ID or date");
				return;
			}
			if (!smallInfo.containsKey("timeslot_id") || !smallInfo.containsKey("duration")) {
				response.sendRedirect(
						request.getContextPath() + "/pages/bookingPage.jsp?errorMessage=Incomplete service data");
				return;
			}

			// Here, I need to show the 
			
			
			List<Integer> timeslots = TimeSlotDAO.getTimeslotsByTimeslotId((int) smallInfo.get("timeslot_id"));

			// add lunch break to timeslots
			timeslots.add(4, 999);

			// check the duration of the service and format the data
			List<Integer> formattedTimeslots = formatTimeslots(timeslots, (int) smallInfo.get("duration"));
			// This array has either 0, 1, or 2.
			// 0 if someome booked already,
			// 1 if the slot is available but not enough time for the service duration
			// 2 if the slot is available and behind got enough time for the service
			// duration

			// pass the data to the frontend in linked hashmap
			Map<String, Object> timeslotAvailability = new LinkedHashMap<>();
			timeslotAvailability.put("booking_date", date);
			timeslotAvailability.put("8am-9am", formattedTimeslots.get(0));
			timeslotAvailability.put("9am-10am", formattedTimeslots.get(1));
			timeslotAvailability.put("10am-11am", formattedTimeslots.get(2));
			timeslotAvailability.put("11am-12pm", formattedTimeslots.get(3));
			timeslotAvailability.put("12pm-1pm", formattedTimeslots.get(4));
			timeslotAvailability.put("1pm-2pm", formattedTimeslots.get(5));
			timeslotAvailability.put("2pm-3pm", formattedTimeslots.get(6));
			timeslotAvailability.put("3pm-4pm", formattedTimeslots.get(7));
			timeslotAvailability.put("4pm-5pm", formattedTimeslots.get(8));
			timeslotAvailability.put("5pm-6pm", formattedTimeslots.get(9));

			// Store the list in the session attribute

			session.setAttribute("timeslot-availability", timeslotAvailability);
			session.setAttribute("service-id", serviceId);
			session.setAttribute("timeslot-id", smallInfo.get("timeslot_id"));

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
