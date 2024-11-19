<%--
-Author: Win Moe Oo
-Date: 5/11/2024
-Copyright Notice: N/A
-@(#)
Description:
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JAD-Washy-Washy</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
<link rel="icon" href="<%=request.getContextPath()%>/assets/favicon.ico" type="image/x-icon">
</head>
<body>
    <div><%@ include file="navbar.jsp"%></div>
    
    <div>
        <% 
            String dbUrl = "jdbc:postgresql://ep-empty-poetry-a5gsev0p.us-east-2.aws.neon.tech:5432/neondb";
            String dbUser = "neondb_owner";
            String dbPassword = "JhCEdNBG5O6Q";
            String dbClass = "org.postgresql.Driver";
            
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            
            try {
                // Step 1: Load JDBC Driver
                Class.forName(dbClass);
                
                // Step 2: Establish connection to URL
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                
                // Step 3: Create Statement object
                stmt = conn.createStatement();
                
                // Step 4: Execute SQL Command
                String sqlStr = "SELECT * FROM users";
                rs = stmt.executeQuery(sqlStr);
                
                // Step 5: Loop through each record and display data
        %>
                <table border="1">
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                    </tr>
                    <%
                        while (rs.next()) {
                            int user_id = rs.getInt("user_id");
                            String username = rs.getString("username");
                    %>
                    <tr>
                        <td><%= user_id %></td>
                        <td><%= username %></td>
                    </tr>
                    <%
                        } // end while
                    %>
                </table>
        <%
            } catch (ClassNotFoundException e) {
                out.println("JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                out.println("SQL Exception: " + e.getMessage());
            } finally {
                // Close resources
                if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
                if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
                if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
            }
        %>
    </div>
</body>
</html>