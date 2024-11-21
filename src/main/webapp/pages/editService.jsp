<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, backend.service" %>
<!DOCTYPE html>

<%
    // Cookie-based session management
    Cookie[] cookies = request.getCookies();
    boolean isLoggedIn = false;
    boolean isAdmin = false;

    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("isLoggedIn".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                isLoggedIn = true;
            }
            if ("isAdmin".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                isAdmin = true;
            }
        }
    }

    // Redirect if not logged in
    if (!isLoggedIn) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    // Redirect if not an admin
    if (!isAdmin) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>

<html>
<head>
    <meta charset="UTF-8">
    <title>Create Service</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
    <div class="container">
        <h2>Create Service for Category: <%= session.getAttribute("categoryName") %></h2>

        <!-- Display success or error messages -->
        <%
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (successMessage != null) {
        %>
            <p style="color: green;"><%= successMessage %></p>
        <%
            }
            if (errorMessage != null) {
        %>
            <p style="color: red;"><%= errorMessage %></p>
        <%
            }
        %>

        <form action="ServiceServlet" method="post">
            <div>
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" placeholder="Enter service name" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" placeholder="Enter service price" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" placeholder="Enter duration" required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" placeholder="Enter description" required />
            </div>
            <div>
                <input type="submit" value="Add Service" />
            </div>
        </form>

        <h3>Available Services for <%= session.getAttribute("categoryName") %></h3>
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
                    List<service> services = (List<service>) request.getAttribute("services");
                    if (services != null) {
                        for (service service : services) {
                %>
                <tr>
                    <td><%= service.getId() %></td>
                    <td><%= service.getName() %></td>
                    <td><%= service.getPrice() %></td>
                    <td><%= service.getDurationInHour() %></td>
                    <td><%= service.getDescription() %></td>
                    <td>
                        <form action="serviceEditor.jsp" method="get" style="display: inline;">
                            <input type="hidden" name="serviceId" value="<%= service.getId() %>" />
                            <button type="submit" class="btn btn-edit">Edit</button>
                        </form>
                        <form action="deleteService.jsp" method="post" style="display: inline;"
                              onsubmit="return confirm('Are you sure you want to delete this service?');">
                            <input type="hidden" name="serviceId" value="<%= service.getId() %>" />
                            <button type="submit" class="btn btn-delete">Delete</button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>