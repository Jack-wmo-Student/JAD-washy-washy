package backend;

import java.util.Enumeration;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



public class feedbackLogic extends HttpServlet {
	private final String dbClass = System.getenv("DB_CLASS");
	private final String dbUrl = System.getenv("DB_URL");
	private final String dbPassword = System.getenv("DB_PASSWORD");
	private final String dbUser = System.getenv("DB_USER");
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		
    }
	
	// Add inside the database
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		// Get the request names
		Enumeration<String> parameterNames = request.getParameterNames();

	    // Set class
	    try {
			Class.forName(dbClass);
		} catch (ClassNotFoundException e) {
			System.out.printf("Could not find the correct class for the database." , e);
			e.printStackTrace();
		}
	    
	    // Create the query for the database
	    String query = "INSERT INTO response(feedback_id, question_id, response_value) VALUES (?, ?, ?)";
	    
	    // Set connection with database and query
	    try(Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	    		PreparedStatement stmt = conn.prepareStatement(query);) {
	    	
	    	// Loop through the map and send query
	    	while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();
	            String paramValue = request.getParameter(paramName); // Get the value of the parameter
	            
	            int questionId = Integer.parseInt(paramName);

	            // Process each parameter
	            stmt.setInt(1, 2);
	            stmt.setInt(2, questionId);
	            stmt.setString(3, paramValue);
	            
	            // Execute Data
	            stmt.executeUpdate();
	        }
	    	
	    	// show a thank you message, redirect to next page
	        request.setAttribute("feedbackSuccess", true);
	        request.getRequestDispatcher("feedback.jsp").forward(request, response);
	    	
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
    }
}





