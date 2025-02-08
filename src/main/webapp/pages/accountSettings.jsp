<%@ page
	import="MODEL.CLASS.User"%>
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

	<form class="settings-form"
		action="<%=request.getContextPath()%>/handleUpdateUser" method="POST">
		<h2>Account Settings</h2>
		<%-- Display error message if exists --%>
		<%
		String error = (String) request.getAttribute("error");
		%>
		<%
		if (error != null) {
		%>
		<p class="error"><%=error%></p>
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
</body>
</html>
