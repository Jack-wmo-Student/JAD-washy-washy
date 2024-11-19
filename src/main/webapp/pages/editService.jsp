<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Service</title>
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
	background: #fff;
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

input[type="text"], input[type="number"] {
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

th, td {
	border: 1px solid #ddd;
	padding: 10px;
	text-align: center;
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
	border: none;
	border-radius: 4px;
	cursor: pointer;
	margin: 2px;
}

.btn-edit {
	background-color: #4CAF50;
	color: white;
}

.btn-delete {
	background-color: #f44336;
	color: white;
}

.btn:hover {
	opacity: 0.9;
}

.alert {
	padding: 10px;
	margin-bottom: 20px;
	border-radius: 5px;
	color: white;
	font-weight: bold;
}

.alert-success {
	background-color: #4CAF50;
}

.alert-error {
	background-color: #f44336;
}
</style>
</head>
<body>
	<div class="container">
		<%
        String categoryId = (String) session.getAttribute("categoryId");
        String categoryName = (String) session.getAttribute("categoryName");

        // If session attributes are not set, fetch and store them
        if (categoryId == null) {
            categoryId = request.getParameter("categoryId");
            session.setAttribute("categoryId", categoryId);
        }

        if (categoryName == null) {
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                Class.forName("DB_CLASS");
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                String fetchSQL = "SELECT category_name FROM service_categories WHERE category_id = ?";
                ps = conn.prepareStatement(fetchSQL);
                ps.setInt(1, Integer.parseInt(categoryId));
                rs = ps.executeQuery();

                if (rs.next()) {
                    categoryName = rs.getString("category_name");
                    session.setAttribute("categoryName", categoryName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            }
        }
        %>

		<h2>
			Create Service for Category:
			<%= categoryName %></h2>
		<%
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String serviceName = request.getParameter("serviceName");
            String servicePrice = request.getParameter("servicePrice");
            String serviceDuration = request.getParameter("serviceDuration");
            String serviceDescription = request.getParameter("serviceDescription");

            try {
                Class.forName("org.postgresql.Driver");
                String dbUrl = System.getenv("DB_URL");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                String insertSQL = "INSERT INTO service (service_name, category_id, price, duration_in_hour, service_description) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insertSQL);
                ps.setString(1, serviceName);
                ps.setInt(2, Integer.parseInt(categoryId));
                ps.setDouble(3, Double.parseDouble(servicePrice));
                ps.setInt(4, Integer.parseInt(serviceDuration));
                ps.setString(5, serviceDescription);
                ps.executeUpdate();

                out.println("<div class='alert alert-success'>Service added successfully!</div>");
                ps.close();
                conn.close();
            } catch (Exception e) {
                out.println("<div class='alert alert-error'>Error adding service. Please try again.</div>");
                e.printStackTrace();
            }
        }
        %>

		<form action="editService.jsp" method="post">
			<div>
				<label for="serviceName">Service Name:</label> <input type="text"
					name="serviceName" placeholder="Enter service name" required />
			</div>
			<div>
				<label for="servicePrice">Service Price:</label> <input
					type="number" name="servicePrice" placeholder="Enter service price"
					required />
			</div>
			<div>
				<label for="serviceDuration">Service Duration (in hours):</label> <input
					type="number" name="serviceDuration" placeholder="Enter duration"
					required />
			</div>
			<div>
				<label for="serviceDescription">Service Description:</label> <input
					type="text" name="serviceDescription"
					placeholder="Enter description" required />
			</div>
			<div>
				<input type="submit" value="Add Service" />
			</div>
		</form>

		<h3>
			Available Services for
			<%= categoryName %></h3>
		<table>
			<thead>
				<tr>
					<th>Service ID</th>
					<th>Service Name</th>
					<th>Price</th>
					<th>Duration (Hours)</th>
					<th>Description</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				<%
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    Class.forName("org.postgresql.Driver");
                    String dbUrl = System.getenv("DB_URL");
                    String dbUser = System.getenv("DB_USER");
                    String dbPassword = System.getenv("DB_PASSWORD");
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                    String fetchServicesSQL = "SELECT * FROM service WHERE category_id = ?";
                    ps = conn.prepareStatement(fetchServicesSQL);
                    ps.setInt(1, Integer.parseInt(categoryId));
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        int serviceId = rs.getInt("service_id");
                        String serviceName = rs.getString("service_name");
                        double price = rs.getDouble("price");
                        int duration = rs.getInt("duration_in_hour");
                        String description = rs.getString("service_description");
                %>
				<tr>
					<td><%= serviceId %></td>
					<td><%= serviceName %></td>
					<td><%= price %></td>
					<td><%= duration %></td>
					<td><%= description %></td>
					<td>
						<form action="serviceEditor.jsp" method="get"
							style="display: inline;">
							<input type="hidden" name="serviceId" value="<%= serviceId %>" />
							<button type="submit" class="btn btn-edit">Edit</button>
						</form>
						<form action="deleteService.jsp" method="post"
							style="display: inline;"
							onsubmit="return confirm('Are you sure you want to delete this service?');">
							<input type="hidden" name="serviceId" value="<%= serviceId %>" />
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
			</tbody>
		</table>
	</div>
</body>
</html>