<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Service</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 50px auto;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        h2 {
            text-align: center;
            color: #333;
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
        // Retrieve parameters and initialize variables
        String serviceId = request.getParameter("serviceId");
        String categoryId = request.getParameter("categoryId");
        String returnUrl = request.getParameter("returnUrl");

        // Use session attributes to reduce redundant database queries
        String serviceName = (String) session.getAttribute("serviceName");
        Double servicePrice = (Double) session.getAttribute("servicePrice");
        Integer serviceDuration = (Integer) session.getAttribute("serviceDuration");
        String serviceDescription = (String) session.getAttribute("serviceDescription");

        if (serviceName == null || servicePrice == null || serviceDuration == null || serviceDescription == null) {
            try {
                String dbUrl = System.getenv("DB_URL");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                // Load database driver
                Class.forName("org.postgresql.Driver");

                // Connect to the database
                try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                    String fetchSQL = "SELECT * FROM service WHERE service_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(fetchSQL)) {
                        ps.setInt(1, Integer.parseInt(serviceId));
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                // Fetch service details
                                serviceName = rs.getString("service_name");
                                servicePrice = rs.getDouble("price");
                                serviceDuration = rs.getInt("duration_in_hour");
                                serviceDescription = rs.getString("service_description");

                                // Store fetched details in the session
                                session.setAttribute("serviceName", serviceName);
                                session.setAttribute("servicePrice", servicePrice);
                                session.setAttribute("serviceDuration", serviceDuration);
                                session.setAttribute("serviceDescription", serviceDescription);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<div class='alert alert-error'>Error fetching service details.</div>");
            }
        }
        %>

        <h2>Edit Service</h2>
        <form action="serviceEditor.jsp" method="post">
            <input type="hidden" name="serviceId" value="<%= serviceId %>" />
            <input type="hidden" name="categoryId" value="<%= categoryId %>" />
            <input type="hidden" name="returnUrl" value="<%= returnUrl %>" />
            <div>
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" value="<%= serviceName != null ? serviceName : "" %>" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" value="<%= servicePrice != null ? servicePrice : 0.0 %>" step="0.01" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" value="<%= serviceDuration != null ? serviceDuration : 0 %>" required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" value="<%= serviceDescription != null ? serviceDescription : "" %>" required />
            </div>
            <div>
                <input type="submit" value="Update Service" />
            </div>
        </form>

        <%
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                String updatedServiceName = request.getParameter("serviceName");
                Double updatedServicePrice = Double.parseDouble(request.getParameter("servicePrice"));
                Integer updatedServiceDuration = Integer.parseInt(request.getParameter("serviceDuration"));
                String updatedServiceDescription = request.getParameter("serviceDescription");

                // Update the database
                String dbUrl = System.getenv("DB_URL");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                Class.forName("org.postgresql.Driver");
                try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                    String updateSQL = "UPDATE service SET service_name = ?, price = ?, duration_in_hour = ?, service_description = ? WHERE service_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                        ps.setString(1, updatedServiceName);
                        ps.setDouble(2, updatedServicePrice);
                        ps.setInt(3, updatedServiceDuration);
                        ps.setString(4, updatedServiceDescription);
                        ps.setInt(5, Integer.parseInt(serviceId));
                        ps.executeUpdate();
                    }
                }

                // Redirect after successful update
                response.sendRedirect(returnUrl != null ? returnUrl : "editService.jsp?categoryId=" + categoryId);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<div class='alert alert-error'>Error updating service. Please try again.</div>");
            }
        }
        %>

        <a href="<%= returnUrl != null ? returnUrl : "editService.jsp?categoryId=" + categoryId %>">Back to Services</a>
    </div>
</body>
</html>