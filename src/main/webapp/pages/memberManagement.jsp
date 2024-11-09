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
	String dbUrl = "jdbc:postgresql://ep-empty-poetry-a5gsev0p.us-east-2.aws.neon.tech:5432/neondb";
	String dbUser = "neondb_owner";
	String dbPassword = "JhCEdNBG5O6Q";
	String dbClass = "org.postgresql.Driver";
	
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