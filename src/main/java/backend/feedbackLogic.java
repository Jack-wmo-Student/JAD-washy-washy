package backend;

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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			System.out.printf("Connection drive issue." , e);
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





