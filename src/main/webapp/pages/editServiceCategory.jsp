<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Category</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f9f9f9;
        margin: 0;
        padding: 0;
    }

    .container {
        max-width: 800px;
        margin: 50px auto;
        background: #ffffff;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        padding: 20px;
    }

    h2, h3 {
        text-align: center;
        color: #333;
    }

    form {
        margin-bottom: 20px;
    }

    form div {
        margin-bottom: 15px;
    }

    label {
        display: block;
        font-weight: bold;
        margin-bottom: 5px;
    }

    input[type="text"] {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 14px;
    }

    input[type="submit"], button {
        padding: 10px 20px;
        background-color: #4CAF50;
        color: white;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 16px;
    }

    input[type="submit"]:hover, button:hover {
        background-color: #45a049;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }

    table, th, td {
        border: 1px solid #ddd;
    }

    th, td {
        padding: 10px;
        text-align: center;
    }

    th {
        background-color: #f4f4f4;
        font-weight: bold;
    }

    tr:nth-child(even) {
        background-color: #f9f9f9;
    }

    .actions {
        display: flex;
        justify-content: center;
        gap: 10px;
    }

    .btn-edit {
        background-color: #007BFF;
    }

    .btn-edit:hover {
        background-color: #0056b3;
    }

    .btn-delete {
        background-color: #f44336;
    }

    .btn-delete:hover {
        background-color: #d32f2f;
    }
</style>
</head>
<body>
    <div class="container">
        <h2>Create Service Category</h2>
        <%
            // Check if user is logged in
            String userRole = (String) session.getAttribute("role");
            if (userRole == null || !userRole.equals("admin")) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Database setup
            String dbClass = "DB_CLASS";
            String dbUrl = System.getenv("DB_URL");
            String dbPassword = System.getenv("DB_PASSWORD");
            String dbUser = System.getenv("DB_USER");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            // Handle form submission to create a new category
            if (request.getMethod().equalsIgnoreCase("POST")) {
                String categoryName = request.getParameter("categoryName");
                String categoryDescription = request.getParameter("categoryDescription");

                try {
                    Class.forName(dbClass);
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                    String insertSQL = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
                    ps = conn.prepareStatement(insertSQL);
                    ps.setString(1, categoryName);
                    ps.setString(2, categoryDescription);
                    ps.executeUpdate();

                    // Add a success message to session
                    session.setAttribute("successMessage", "Category added successfully!");
                } catch (Exception e) {
                    session.setAttribute("errorMessage", "Error: " + e.getMessage());
                } finally {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                }

                // Redirect to avoid duplicate form submissions
                response.sendRedirect("createCategory.jsp");
                return;
            }
        %>

        <!-- Display success or error messages -->
        <%
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (successMessage != null) {
        %>
            <p style="color: green;"><%= successMessage %></p>
        <%
                session.removeAttribute("successMessage");
            }
            if (errorMessage != null) {
        %>
            <p style="color: red;"><%= errorMessage %></p>
        <%
                session.removeAttribute("errorMessage");
            }
        %>

        <!-- Category form -->
        <form action="createCategory.jsp" method="post">
            <div>
                <label for="categoryName">Category Name:</label>
                <input type="text" name="categoryName" required />
            </div>
            <div>
                <label for="categoryDescription">Category Description:</label>
                <input type="text" name="categoryDescription" required />
            </div>
            <div>
                <input type="submit" value="Add Category" />
            </div>
        </form>

        <h3>Existing Categories</h3>
        <table>
            <tr>
                <th>Category ID</th>
                <th>Category Name</th>
                <th>Category Description</th>
                <th>Actions</th>
            </tr>
            <%
                try {
                    Class.forName(dbClass);
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                    String fetchSQL = "SELECT * FROM category";
                    ps = conn.prepareStatement(fetchSQL);
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        int categoryId = rs.getInt("category_id");
                        String categoryName = rs.getString("category_name");
                        String categoryDescription = rs.getString("category_description");
            %>
            <tr>
                <td><%=categoryId%></td>
                <td><%=categoryName%></td>
                <td><%=categoryDescription%></td>
                <td>
                    <div class="actions">
                        <form action="editService.jsp" method="get" style="display: inline;">
                            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
                            <button type="submit" class="btn-edit">Edit</button>
                        </form>
                        <form action="deleteServiceCategory.jsp" method="get" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this category?');">
                            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
                            <button type="submit" class="btn-delete">Delete</button>
                        </form>
                    </div>
                </td>
            </tr>
            <%
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                }
            %>
        </table>
    </div>
</body>
</html>