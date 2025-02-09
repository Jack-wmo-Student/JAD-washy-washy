<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, utils.sessionUtils, MODEL.CLASS.Service" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Service</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
    <%
    // Authentication checks
    if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        request.setAttribute("error", "You must log in first.");
        request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
        return;
    }

    // Admin-only access
    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    }

    // Retrieve service details from request parameters
    String serviceIdStr = request.getParameter("serviceId");
    String serviceName = request.getParameter("serviceName");
    String servicePrice = request.getParameter("servicePrice");
    String serviceDuration = request.getParameter("serviceDuration");
    String serviceDescription = request.getParameter("serviceDescription");
    String serviceImage = request.getParameter("serviceImage");
    String categoryId = request.getParameter("categoryId");

    // Validate input parameters
    if (serviceIdStr == null || serviceIdStr.trim().isEmpty() || 
        serviceName == null || serviceName.trim().isEmpty() || 
        servicePrice == null || servicePrice.trim().isEmpty() || 
        serviceDuration == null || serviceDuration.trim().isEmpty() || 
        serviceDescription == null || serviceDescription.trim().isEmpty() ||
        categoryId == null || categoryId.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
        return;
    }
    %>

    <div class="container">
        <h2>Edit Service</h2>

        <!-- Display error message if any -->
        <%     
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
        %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <%
            session.removeAttribute("errorMessage");
        }  
        %>

        <!-- Display success message if any -->
        <%     
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
        %>
            <div class="alert alert-success"><%= successMessage %></div>
        <%
            session.removeAttribute("successMessage");
        }  
        %>
        
        <form action="<%= request.getContextPath() %>/ServiceController/update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="serviceId" value="<%= serviceIdStr %>" />
            <input type="hidden" name="categoryId" value="<%= categoryId %>" />
            
            <div class="form-group">
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" value="<%= serviceName %>" required />
            </div>
            
            <div class="form-group">
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" step="0.01" value="<%= servicePrice %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" value="<%= serviceDuration %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" value="<%= serviceDescription %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceImage">Service Image:</label>
                <% if (serviceImage != null && !serviceImage.trim().isEmpty()) { %>
                    <div>
                        <img src="<%= request.getContextPath() + "/" + serviceImage %>" width="100" />
                        <p>Current Image</p>
                    </div>
                <% } %>
                <input type="file" name="serviceImage" accept="image/*" />
            </div>
            
            <div class="form-actions">
                <input type="submit" value="Update Service" class="btn btn-primary" />
                <a href="<%= request.getContextPath() %>/pages/editService.jsp?categoryId=<%= categoryId %>" 
                   class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>