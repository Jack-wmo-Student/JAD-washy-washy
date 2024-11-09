<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Member</title>
</head>
<body>
    <div align="center">
        <h2>Edit Member</h2>
        <%
        String dbUrl = "jdbc:postgresql://ep-empty-poetry-a5gsev0p.us-east-2.aws.neon.tech:5432/neondb";
        String dbUser = "neondb_owner";
        String dbPassword = "JhCEdNBG5O6Q";
        String dbClass = "org.postgresql.Driver";

        String memberIdStr = request.getParameter("id");
        int memberId = 0;  // Use `memberId` as the primary variable
        String memberName = "";

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            // Parse `memberIdStr` to an integer
            memberId = Integer.parseInt(memberIdStr);

            // Step 1: Load JDBC Driver
            Class.forName(dbClass);

            // Step 2: Establish connection
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Step 3: Prepare SQL Command
            String sqlStr = "SELECT * FROM users WHERE user_id = ?";
            preparedStatement = conn.prepareStatement(sqlStr);
            preparedStatement.setInt(1, memberId); // Use `memberId` as an integer in the query

            // Step 4: Execute SQL Command
            rs = preparedStatement.executeQuery();

            // Step 5: Process Result
            if (rs.next()) {
                memberId = rs.getInt("user_id");  // `user_id` from the database
                memberName = rs.getString("username"); // `username` from the database
            }
        } catch (NumberFormatException e) {
            out.println("Invalid member ID format.");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        } finally {
            // Step 6: Close resources
            try {
                if (rs != null) rs.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        %>
        <form action="updateMember.jsp" method="post">
            <!-- Use `memberId` for the hidden input and displayed ID -->
            <input type="hidden" name="id" value="<%= memberId %>" />
            <table border="1">
                <tr>
                    <td>ID</td>
                    <td><%= memberId %></td>
                </tr>
                <tr>
                    <td>Name:</td>
                    <td><input type="text" name="memberName" value="<%= memberName %>" /></td>
                </tr>
                <!-- Add more fields if necessary -->
            </table>
            <br />
            <input type="submit" value="Update">
        </form>
    </div>
</body>
</html>