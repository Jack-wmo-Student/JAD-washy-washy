<%@ page import="MODEL.CLASS.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Account Settings</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/navbar.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/accountSettings.css">
<link rel="icon"
	href="<%=request.getContextPath()%>/assets/icons/favicon.ico"
	type="image/x-icon">
</head>
<body>
	<%@include file="/component/navbar.jsp"%>
	<%
	if (session == null || session.getAttribute("currentUser") == null) {
		response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
		return;
	}
	User user = (User) session.getAttribute("currentUser");
	String username = user.getUsername();
	%>

	<form id="settingsForm" class="settings-form">
		<h2>Account Settings</h2>

		<%-- Display error message if exists --%>
		<%
		String error = request.getParameter("error");
		%>
		<%
		if (error != null) {
		%>
		<p class="error" style="color: red; font-weight: bold;"><%=error%></p>
		<%
		}
		%>


		<!-- Username -->
		<label for="username">Username</label> <input type="text"
			id="username" name="username" value="<%=username%>" required>

		<!-- Password -->
		<label for="password">Password</label> <input type="password"
			id="password" name="password" placeholder="Enter a new password">

		<!-- Confirm Password -->
		<label for="confirm-password">Confirm Password</label> <input
			type="password" id="confirm-password" name="confirmPassword"
			placeholder="Confirm your new password">

		<!-- Save Changes Button -->
		<button type="submit">Update Details</button>
	</form>

	<script>
	document.getElementById("settingsForm").addEventListener("submit", function(event) {
	    event.preventDefault(); // Prevent normal form submission

	    const formData = new FormData(this);
	    const jsonData = JSON.stringify(Object.fromEntries(formData)); // Convert form data to JSON

	    fetch('<%=request.getContextPath()%>/UserController', {
	        method: 'PUT', // ✅ Send a real PUT request
	        body: jsonData,
	        headers: { 'Content-Type': 'application/json' }
	    })
	    .then(response => {
	        // ✅ Redirects are now handled by the server
	        if (response.redirected) {
	            window.location.href = response.url; // Redirect to success/error page
	        } else {
	            alert("Unexpected response from server.");
	        }
	    })
	    .catch(error => {
	        console.error("Request Failed:", error);
	        alert("Error updating user details. Please try again.");
	    });
	});
</script>

</body>
</html>
