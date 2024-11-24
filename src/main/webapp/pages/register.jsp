<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/registerPage.css">
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="register-container">
        <h1>Register</h1>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <p style="color: red;"><%= error %></p>
        <% } %>
        <form action="<%=request.getContextPath()%>/handleRegisterUser" method="POST">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" placeholder="Enter your username" required>
            <br>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Enter your password" required>
            <br>
            <button type="submit">Register</button>
        </form>
        <p>Already have an account? <a href="<%=request.getContextPath()%>/pages/login.jsp">Login here</a></p>
    </div>
</body>
</html>
