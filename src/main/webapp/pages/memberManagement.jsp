<%@page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Member List</title>
    <style>
        /* General body styling */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 900px;
            margin: 50px auto;
            padding: 20px;
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
        }
        th {
            background-color: #007BFF;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        a {
            text-decoration: none;
            padding: 8px 12px;
            background-color: #4CAF50;
            color: white;
            border-radius: 5px;
            font-size: 14px;
        }
        a:hover {
            background-color: #45a049;
        }
        .error-message {
            color: red;
            font-weight: bold;
            margin-top: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Member List</h1>
        <%
            String dbClass = "DB_CLASS";
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