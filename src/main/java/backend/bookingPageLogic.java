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
	  		
	  		
	  		// Fetch Data 1. Get the services
	  		
	  		// Fetch Data 2. Get the Service Name, Booking Id, Timeslot Id by using user Id
	  		
	  		// Fetch Data 3. Get the timeslots by using booking Ids
	  		
	  		
	  		
	  		
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
}









































