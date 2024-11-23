<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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

        <form action="<%=request.getContextPath()%>/EditServiceServlet" method="post">
            <input type="hidden" name="serviceId" value="<%=session.getAttribute("serviceId")%>" />
            <input type="hidden" name="returnUrl" value="<%=request.getParameter("returnUrl") != null ? request.getParameter("returnUrl") : "serviceList?categoryId=" + session.getAttribute("categoryId")%>" />

            <div>
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" value="<%=request.getParameter("serviceName")%>" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" value="<%=request.getParameter("servicePrice")%>" step="10" required />
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

        <a href="<%=request.getParameter("returnUrl") != null ? request.getParameter("returnUrl") : "editService?categoryId=" + session.getAttribute("categoryId")%>">Back to Services</a>
    </div>
</body>
</html>