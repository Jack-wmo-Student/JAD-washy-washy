<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import ="java.sql.*"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Submitting Feedback</title>
	<% 
		// Fetching data from System env
		String dbClass = System.getenv("DB_CLASS");
	    String dbUrl = System.getenv("DB_URL");
	    String dbPassword = System.getenv("DB_PASSWORD");
	    String dbUser = System.getenv("DB_USER");
	%>
</head>
<body>
	<%
		try {
			// Set up the database connection
			Class.forName(dbClass);
			Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Statement stmt = conn.createStatement();
			
			out.print("connection successful!");
			
			
			// Step 5: Execute SQL Command
			// String sqlStr = "SELECT * FROM users";
			// ResultSet rs = stmt.executeQuery(sqlStr);
			
			
			// Step 7: Close connection
			conn.close();
		} catch (Exception e) {
			out.println("Error :" + e);
		}
	%>
	
	Welcome to FeedbackLogic page
</body>
</html>