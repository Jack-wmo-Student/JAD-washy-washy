package CONTROLLER;

// All the imports
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
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.List;

public class feedbackLogic extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final String dbClass = System.getenv("DB_CLASS");
	private final String dbUrl = System.getenv("DB_URL");
	private final String dbPassword = System.getenv("DB_PASSWORD");
	private final String dbUser = System.getenv("DB_USER");
	
	public feedbackLogic() {
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
		
		
		// Get questions, put inside 
		
		ResultSet rs = null;
	  	Connection conn = null;
	  	Statement stmt = null;
		
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
	  		stmt = conn.createStatement();
	  		
	  		// --- fetch Data ---
	  		String sqlStr = "SELECT * FROM question";
	      	rs = stmt.executeQuery(sqlStr);
	      	
	      	System.out.println("We are here");
	      	
		    // Convert ResultSet to a list or array
	      	List<Map<String, String>> questionList = new ArrayList<>();
		    while (rs.next()) {
		    	Map<String, String> question = new HashMap<>();
		        question.put("question_id", rs.getString("question_id"));
		        System.out.printf("These are the questions", rs.getString("question_id"));
		        question.put("question_text", rs.getString("question_text"));
		        question.put("question_type", rs.getString("question_type"));
		        questionList.add(question);
		    }
	      	
		    // Store the list in the request attribute
		    session.setAttribute("questions", questionList);

		    // Forward to the JSP
		    RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/feedbackPage.jsp");
		    dispatcher.forward(request, response);
	      
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		    try {
		        if (rs != null) rs.close();
		        if (stmt != null) stmt.close();
		        if (conn != null) conn.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
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
		List<Integer> bookingIdLists = (List<Integer>) session.getAttribute("bookingIdLists");
		
		// Get the request names
		Enumeration<String> parameterNames = request.getParameterNames();

	    // Set class
	    try {
			Class.forName(dbClass);
		} catch (ClassNotFoundException e) {
			System.out.printf("Connection drive issue." , e);
			e.printStackTrace();
		}
	    
	    // Create the query for the database
	    String createResponsequery = "INSERT INTO response(feedback_id, question_id, response_value) VALUES (?, ?, ?)";
	    String bookingUpdateQuery = "UPDATE booking SET feedback_id = ? WHERE booking_id = ?";
	    
	    // Set connection with database and query the response
	    try(Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	    		PreparedStatement pstmt1 = conn.prepareStatement(bookingUpdateQuery);
	    		PreparedStatement pstmt2 = conn.prepareStatement(createResponsequery);
	    		Statement stmt = conn.createStatement()) {
	    	
	    	// Step 1: Insert a new feedback entry
	        String feedbackInsertQuery = "INSERT INTO feedback DEFAULT VALUES RETURNING feedback_id";
	    	
	       
	    	ResultSet rs = stmt.executeQuery(feedbackInsertQuery);
	    	int feedback_id = 0;
	    	
	    	while(rs.next()) {
	    		feedback_id = rs.getInt("feedback_id");
	    	}
	    	
	    	
	        // Update the booking id
	    	for(Integer bookingId : bookingIdLists) {
                pstmt1.setInt(1, feedback_id);
                pstmt1.setInt(2, bookingId);
                pstmt1.executeUpdate();
            }
 	    		
	    	// Loop through the map and send query
	    	while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();
	            String paramValue = request.getParameter(paramName); // Get the value of the parameter
	            
	            if(paramName != "Booking Id") {
	            	int questionId = Integer.parseInt(paramName);

		            // Process each parameter
		            pstmt2.setInt(1, feedback_id);
		            pstmt2.setInt(2, questionId);
		            pstmt2.setString(3, paramValue);
		            
		            // Execute Data
		            pstmt2.executeUpdate();
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