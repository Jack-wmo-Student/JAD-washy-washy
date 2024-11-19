<%@page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Member List</title>
</head>
<body>
<div align="center">
<%
	String dbClass = "org.postgresql.Driver";
	String dbUrl = System.getenv("DB_URL");
	String dbPassword = System.getenv("DB_PASSWORD");
	String dbUser = System.getenv("DB_USER");
	
	try {
		// Step1: Load JDBC Driver
		Class.forName(dbClass);
		
		// Step 3: Establish connection to URL
		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		
		// Step 4: Create Statement object
		Statement stmt = conn.createStatement();
		
		// Step 5: Execute SQL Command
		String sqlStr = "SELECT * FROM users";
		ResultSet rs = stmt.executeQuery(sqlStr);
%>
	<table border="1">
		<tr>
			<td>ID</td>
			<td>Name</td>
			<td>Action</td>
		</tr>

	<%
        // Loop through each record
        while (rs.next()) {
			int user_id = rs.getInt("user_id");
			String username = rs.getString("username");
    %>
		<tr>
			<td><%= user_id %></td>
			<td><%= username %></td>
			<td><a href="editMember.jsp?id=<%= user_id %>">Edit</a></td>
		</tr>
	<%
        }
		// Step 7: Close resources
		rs.close();
		stmt.close();
		conn.close();
    %>
	</table>
<%
	} catch (Exception e) {
		e.printStackTrace();
		out.println("Error: " + e.getMessage());
	}
%>
</div>
</body>
</html>