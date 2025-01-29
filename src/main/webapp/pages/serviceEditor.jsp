<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="java.util.*, utils.sessionUtils,MODEL.CLASS.service"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Service Editor</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
	
	<% if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        request.setAttribute("error", "You must log in first.");
        request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
        return;
    }

    // Optional: Check if the user is an admin
    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    } %>
	
    <div class="container">
        <h2>Edit Service</h2>

        <!-- Display error message if any -->
        <%     
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
        %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <%
        }  
        %>

        <%-- <form action="<%= request.getContextPath() %>/EditServiceServlet" method="post"> --%>
            <input type="hidden" name="serviceId" />
            <div>
                <label for="serviceName">Service Name:</label> <span><%=request.getParameter("serviceName")%></span>
                <input type="text" name="serviceName"  required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label> <span><%=request.getParameter("servicePrice")%></span>
                <input type="number" name="servicePrice"  step="10" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label> <span><%=request.getParameter("serviceDuration")%></span>
                <input type="number" name="serviceDuration"  required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label> <span><%=request.getParameter("serviceDescription")%></span>
                <input type="text" name="serviceDescription"  required />
            </div>
            <div>
                <input type="submit" value="Update Service" />
            </div>
        </form>
        
        

		<a href="<%= request.getContextPath() %>/pages/editServiceCategory.jsp">Back to Main Page</a>    </div>
</body>
</html>