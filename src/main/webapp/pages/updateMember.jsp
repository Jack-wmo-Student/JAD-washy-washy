<%@page import="java.sql.*"%>
<%
    // Retrieve form parameters
    int userId = Integer.parseInt(request.getParameter("userId"));
    String action = request.getParameter("action");
    boolean currentStatus = Boolean.parseBoolean(request.getParameter("currentStatus"));

    // Database credentials
    String dbClass = "DB_CLASS";
    String dbUrl = System.getenv("DB_URL");
    String dbPassword = System.getenv("DB_PASSWORD");
    String dbUser = System.getenv("DB_USER");

    try {
        // Step1: Load JDBC Driver
        Class.forName(dbClass);

        // Step2: Establish connection
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        // Step3: Prepare the SQL Update Query
        String updateQuery = null;
        if ("toggleAdmin".equals(action)) {
            updateQuery = "UPDATE users SET is_admin = ? WHERE user_id = ?";
        } else if ("toggleBlocked".equals(action)) {
            updateQuery = "UPDATE users SET is_blocked = ? WHERE user_id = ?";
        }

        if (updateQuery != null) {
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setBoolean(1, !currentStatus); // Toggle the current status
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            pstmt.close();
        }

        // Redirect back to the Member List page
        response.sendRedirect("memberList.jsp");
    } catch (Exception e) {
        e.printStackTrace();
        out.println("Error: " + e.getMessage());
    }
%>