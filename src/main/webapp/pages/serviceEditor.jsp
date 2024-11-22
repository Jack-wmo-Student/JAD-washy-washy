<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="java.util.*,model.service, model.category" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>Service Editor</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
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

        <form action="<%=request.getContextPath()%>/editService" method="post">
            <input type="hidden" name="serviceId" value="<%=request.getParameter("serviceId")%>" />
            <input type="hidden" name="categoryId" value="<%=request.getParameter("categoryId")%>" />
            <input type="hidden" name="returnUrl" value="<%=request.getParameter("returnUrl")%>" />

            <div>
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" value="<%=request.getParameter("serviceName")%>" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" value="<%=request.getParameter("servicePrice")%>" step="0.01" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" value="<%=request.getParameter("serviceDuration")%>" required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" value="<%=request.getParameter("serviceDescription")%>" required />
            </div>
            <div>
                <input type="submit" value="Update Service" />
            </div>
        </form>

        <a href="<%=request.getParameter("returnUrl") != null ? request.getParameter("returnUrl") : "serviceList?categoryId=" + request.getParameter("categoryId")%>">Back to Services</a>
    </div>
</body>
</html>