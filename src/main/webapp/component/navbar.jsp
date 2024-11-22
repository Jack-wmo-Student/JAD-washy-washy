<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, model.category"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%-- <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css"> --%>
<title>JAD-Washy-Washy Navbar</title>
</head>
<body>
	<div class="navbar">
		<!-- Navbar Header -->
		<h2>JAD-Washy-Washy</h2>
		<ul class="nav-links">
			<!-- Home Link -->
			<li><a href="<%=request.getContextPath()%>/pages/homePage.jsp">Home</a></li>

			<!-- Admin Dashboard Link -->
			<li><a href="<%=request.getContextPath()%>/CreateCategoryServlet">Admin Dashboard</a></li>
			
			<!-- Feedback Tab for testing -->
			<li><a href="<%=request.getContextPath()%>/feedbackLogic">Feedback</a></li>
			
			<li><a href="<%=request.getContextPath()%>/bookingPage">Book now</a></li>

			<!-- Categories Dropdown using <select> -->
			<li>
				<select class="category-dropdown" onchange="navigateToCategory(this.value)">
					<option value="" disabled selected hidden>Categories</option>
					<%
					// Retrieve category-service map from the session
					Map<category, List<category>> categoryServiceMap = (Map<category, List<category>>) session
							.getAttribute("categoryServiceMap");

					// Dynamically render category options or show fallback message
					if (categoryServiceMap != null && !categoryServiceMap.isEmpty()) {
						for (category cat : categoryServiceMap.keySet()) {
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
				</select>
			</li>
		</ul>

		<!-- Logout Button -->
		<form action="<%=request.getContextPath()%>/logout" method="POST" class="logout-form">
			<button type="submit" class="logout-button">Log Out</button>
		</form>
	</div>

	<script>
		// JavaScript function to navigate to selected category
		function navigateToCategory(categoryId) {
			if (categoryId) {
				window.location.href = categoryId;
			}
		}
	</script>
</body>
</html>