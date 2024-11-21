<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                <input type="text" name="serviceName" value="<%=session.getAttribute("serviceName") != null ? session.getAttribute("serviceName") : ""%>" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" value="<%=session.getAttribute("servicePrice") != null ? session.getAttribute("servicePrice") : 0.0%>" step="0.01" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" value="<%=session.getAttribute("serviceDuration") != null ? session.getAttribute("serviceDuration") : 0%>" required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" value="<%=session.getAttribute("serviceDescription") != null ? session.getAttribute("serviceDescription") : ""%>" required />
            </div>
            <div>
                <input type="submit" value="Update Service" />
            </div>
        </form>

        <a href="<%=request.getParameter("returnUrl") != null ? request.getParameter("returnUrl") : "serviceList?categoryId=" + request.getParameter("categoryId")%>">Back to Services</a>
    </div>
</body>
</html>