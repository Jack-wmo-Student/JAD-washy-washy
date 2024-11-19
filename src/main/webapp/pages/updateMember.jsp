<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Member</title>
</head>
<body>
<div align="center">
<%
  // Retrieve parameters
  String memberId = request.getParameter("id");
  String name = request.getParameter("memberName");  // Matches form input field name

  Connection conn = null;
  PreparedStatement preparedStatement = null;

  try {
	  String dbClass = "org.postgresql.Driver";
	  String connURL = System.getenv("DB_URL");
	  String dbPassword = System.getenv("DB_PASSWORD");
	  String dbUser = System.getenv("DB_USER");
    conn = DriverManager.getConnection(connURL, dbUser, dbPassword);

    // Prepare SQL update statement
    String sql = "UPDATE users SET username = ? WHERE user_id = ?";
    preparedStatement = conn.prepareStatement(sql);
    preparedStatement.setString(1, name);
    preparedStatement.setInt(2, Integer.parseInt(memberId));  // Convert ID to integer if necessary

    // Execute update
    int rowsUpdated = preparedStatement.executeUpdate();

    // Check result and display message
    if (rowsUpdated > 0) {
%>
      <p>Member updated successfully!</p>
<%
    } else {
%>
      <p>Error: Member not found or update failed.</p>
<%
    }
  } catch (Exception e) {
    e.printStackTrace();
    out.println("Error: " + e.getMessage());
  } finally {
    // Close resources
    try {
      if (preparedStatement != null) preparedStatement.close();
      if (conn != null) conn.close();
    } catch (SQLException se) {
      se.printStackTrace();
    }
  }
%>
</div>
</body>
</html>