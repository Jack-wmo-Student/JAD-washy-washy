<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Service Category</title>
</head>
<body>
<%
    String dbClass = "DB_CLASS";
    String dbUrl = System.getenv("DB_URL");
    String dbPassword = System.getenv("DB_PASSWORD");
    String dbUser = System.getenv("DB_USER");
    Connection conn = null;
    PreparedStatement ps = null;

    // Get the category ID to delete
    String categoryId = request.getParameter("categoryId");

    try {
        Class.forName("DB_CLASS");
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        String deleteSQL = "DELETE FROM service_category WHERE category_id = ?";
        ps = conn.prepareStatement(deleteSQL);
        ps.setInt(1, Integer.parseInt(categoryId));
        int rowsDeleted = ps.executeUpdate();

        if (rowsDeleted > 0) {
%>
        <p>Category deleted successfully!</p>
<%
        } else {
%>
        <p>Category not found or could not be deleted.</p>
<%
        }
    } catch (Exception e) {
        e.printStackTrace();
%>
    <p>Error deleting category: <%= e.getMessage() %></p>
<%
    } finally {
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    }
%>
<a href="editServiceCategory.jsp">Back to Categories</a>
</body>
</html>