package CONTROLLER;

import MODEL.DAO.FeedbackDAO;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

// All the imports
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class FeedbackController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FeedbackController() {
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

		// Should check if got booking id or not. if not tell them dun have a booking to fill feedback form or sth
		try {
	  		// Call the DAO and get the question list
			List<Map<String, String>> questionList = FeedbackDAO.getQuestions();

		    // Store the list in the request attribute
		    session.setAttribute("questions", questionList);

		    // Forward to the JSP
		    RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/feedbackPage.jsp");
		    dispatcher.forward(request, response);
	      
		} catch(Exception e) {
			e.printStackTrace();
		} 
    }
	
	// Add inside the database
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
		// Get the booking List from the session
		@SuppressWarnings("unchecked")
		List<Integer> bookingIdLists = (List<Integer>) session.getAttribute("bookingIdLists");
		
		// Get the request names
		Enumeration<String> parameterNames = request.getParameterNames();
	    	    
	    
	    // Set connection with database and query the response
	    try {
	    	// Step 1: Insert a new feedback entry
	        int feedback_id = FeedbackDAO.createFeedbackWithDefaultValues();
	            	
	    	
	        // Update the booking table with feedback id
	        FeedbackDAO.updateBookingWithFeedbackId(bookingIdLists, feedback_id);
 	    		
	    	// Loop through the map and send query
	    	while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();
	            String paramValue = request.getParameter(paramName); // Get the value of the parameter
	            
	            if(paramName != "Booking Id") {
	            	int questionId = Integer.parseInt(paramName);

	            	// Add response to the table
	            	FeedbackDAO.createFeedbackResponse(feedback_id, questionId, paramValue);
	            	
	            }	           
	        }
	    	
	    	System.out.println("Form uploaded, redirecting...");
	    	// show a thank you message, redirect to next page
	        request.setAttribute("feedbackSuccess", true);
	        request.getRequestDispatcher("/pages/feedbackPage.jsp").forward(request, response);
	    	
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
    }
}