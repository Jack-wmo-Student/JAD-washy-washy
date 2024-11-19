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
            margin: 50px auto;
            width: 80%;
            max-width: 800px;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
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
        }
        input[type="submit"] {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        input[type="submit"]:hover {
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
            text-align: center;
            padding: 10px;
        }
        th {
            background-color: #f4f4f4;
            color: #333;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .btn {
            padding: 8px 12px;
            font-size: 14px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin: 2px;
        }
        .btn-edit {
            background-color: #4CAF50;
            color: white;
        }
        .btn-edit:hover {
            background-color: #45a049;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
        .btn-delete:hover {
            background-color: #e41f1f;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Create Service Category</h2>
        <form action="editServiceCategory.jsp" method="post">
            <div>
                <label for="categoryName">Category Name:</label>
                <input type="text" name="categoryName" placeholder="Enter category name" required />
            </div>
            <div>
                <label for="categoryDescription">Category Description:</label>
                <input type="text" name="categoryDescription" placeholder="Enter category description" required />
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
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            // Fetch and display categories
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                String fetchSQL = "SELECT * FROM service_category";
                ps = conn.prepareStatement(fetchSQL);
                rs = ps.executeQuery();

                while (rs.next()) {
                    int categoryId = rs.getInt("category_id");
                    String categoryName = rs.getString("category_name");
                    String categoryDescription = rs.getString("category_description");
            %>
            <tr>
                <td><%= categoryId %></td>
                <td><%= categoryName %></td>
                <td><%= categoryDescription %></td>
                <td>
                    <form action="editService.jsp" method="get" style="display:inline;">
                        <input type="hidden" name="categoryId" value="<%= categoryId %>" />
                        <button type="submit" class="btn btn-edit">Edit</button>
                    </form>
                    <form action="deleteServiceCategory.jsp" method="get" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this category?');">
                        <input type="hidden" name="categoryId" value="<%= categoryId %>" />
                        <button type="submit" class="btn btn-delete">Delete</button>
                    </form>
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