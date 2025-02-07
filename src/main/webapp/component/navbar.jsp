<%@ page
	import="java.util.List, java.util.Map,MODEL.CLASS.Category, utils.sessionUtils"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>JAD-Washy-Washy Navbar</title>
</head>
<body>
	<div class="navbar">
		<!-- Navbar Header -->
		<h2>JAD-Washy-Washy</h2>
		<ul class="nav-links">
			<!-- Home Link -->
			<li><a href="<%=request.getContextPath()%>/categoryService">Home</a></li>

			<!-- Admin Dashboard Link -->
			<%
			// Check if user is an admin from the session
			Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
			if (isAdmin != null && isAdmin) { // Check if user is an admin
			%>

			<li><a href="<%=request.getContextPath()%>/UserServlet">Admin

					Dashboard</a></li>

			<%
			}
			%>
			<!-- 			<!-- Feedback Tab -->
			<%-- 			<li><a href="<%=request.getContextPath()%>/feedbackLogic">Feedback</a></li> --%>

			<li><a href="<%=request.getContextPath()%>/bookingPage">Book
					now</a></li>

			<!-- Categories Dropdown using <select> -->
			<li><select class="category-dropdown"
				onchange="navigateToCategory(this.value)">
					<option value="" disabled selected hidden>Categories</option>
					<%
					@SuppressWarnings("unchecked")
					// Retrieve category-service map from the session
					Map<Category, List<Category>> categoryServiceMap = (Map<Category, List<Category>>) session
							.getAttribute("categoryServiceMap");

					// Dynamically render category options or show fallback message
					if (categoryServiceMap != null && !categoryServiceMap.isEmpty()) {
						for (Category cat : categoryServiceMap.keySet()) {
							String categoryName = (cat.getName() != null) ? cat.getName().trim() : "Unknown Category";
					%>
					<option value="#category-<%=cat.getId()%>"><%=categoryName%></option>
					<%
					}
					} else {
					%>
					<option value="">No categories available</option>
					<%
					}
					%>
			</select></li>
		</ul>

		<!-- Login/Logout Button -->
		<%
		// Use sessionUtils.isLoggedIn to check login status
		if (sessionUtils.isLoggedIn(request, "isLoggedIn") && request.getSession(false)!= null && session.getAttribute("currentUser") != null) { // Assuming "isLoggedIn" is the cookie name
		%>
		<div class="profile-dropdown-container">
			<select class="dropdown" onchange="handleProfileAction(this.value)">
				<option value="" disabled selected hidden>Profile</option>
				<option
					value="<%=request.getContextPath()%>/pages/accountSettings.jsp">Account
					Settings</option>
				<option value="<%=request.getContextPath()%>/logout">Log
					Out</option>
			</select>
		</div>
		<%
		} else {
		%>
		<form action="<%=request.getContextPath()%>/pages/login.jsp"
			method="GET" class="login-form">
			<button type="submit" class="login-button">Log In / Sign Up</button>
		</form>
		<%
		}
		%>
	</div>

	<script>
		// JavaScript function to navigate to selected category
		function navigateToCategory(categoryId) {
			if (categoryId) {
				window.location.href = categoryId;
			}
		}
		function handleProfileAction(actionUrl) {
			if (actionUrl) {
				window.location.href = actionUrl;
			}
		}
	</script>
</body>
</html>
