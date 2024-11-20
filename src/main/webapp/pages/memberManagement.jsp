<%@page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Member List</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/memberManagement.css">
</head>
<body>
    <div class="container">
        <h1>Member List</h1>
        <%
            String dbClass = System.getenv("DB_CLASS");
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
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Admin</th>
                    <th>Blocked</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    // Loop through each record
                    while (rs.next()) {
                        int user_id = rs.getInt("user_id");
                        String username = rs.getString("username");
                        boolean isAdmin = rs.getBoolean("is_admin");
                        boolean isBlocked = rs.getBoolean("is_blocked");
                %>
                <tr>
                    <td><%= user_id %></td>
                    <td><%= username %></td>
                    <td><%= isAdmin %></td>
                    <td><%= isBlocked %></td>
                    <td>
                        <form action="memberManagement.jsp" method="get"
							style="display: inline;">
							<button type="submit" class="btn btn-edit">Edit</button>
						</form>
                    </td>
                </tr>
                <%
                    }
                    // Step 7: Close resources
                    rs.close();
                    stmt.close();
                    conn.close();
                %>
            </tbody>
        </table>
        <%
            } catch (Exception e) {
                e.printStackTrace();
        %>
        <div class="error-message">
            Error: <%= e.getMessage() %>
        </div>
        <%
            }
        %>
    </div>
</body>
</html>