<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/loginPage.css">
<link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="login-container">
        <h1>Login</h1>
        <%-- Display error message if exists --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <p class="error"><%= error %></p>
        <% } %>
        <form action="<%=request.getContextPath()%>/landingPage" method="POST">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" placeholder="Enter your username" required>
            
            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Enter your password" required>
            
            <button type="submit">Login</button>
        </form>
        <div class="footer">
            <p>Don't have an account? <a href="<%=request.getContextPath()%>/pages/register.jsp">Register here</a></p>
        </div>
    </div>
</body>
</html>
