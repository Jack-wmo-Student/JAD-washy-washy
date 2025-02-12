<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.util.*,MODEL.CLASS.Category,MODEL.CLASS.Service, utils.sessionUtils"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>Create Category</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
	<link rel="stylesheet"href="<%=request.getContextPath()%>/assets/serviceManagement.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
</head>
<body>
	<%
	if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
		request.setAttribute("error", "You must log in first.");
		request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
		return;
	}

	// Optional: Check if the user is an admin
	if (!sessionUtils.isAdmin(request)) {
		response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
		return;
	}
	%>
	<div>
		<%@ include file="/component/navbar.jsp"%>
	</div>
	<div class="container">
           <!--  Side bar -->
		<div>
    		<%@ include file="../component/adminSidebar.jsp" %>
    	</div>
		<h2>Create Service Category</h2>

		<!-- Display success or error messages -->
		<%
			String successMessage = (String) request.getAttribute("successMessage");
			String errorMessage = (String) request.getAttribute("errorMessage");
			if (successMessage != null) {
		%>
			<p style="color: green;"><%=successMessage%></p>
		<%
		}
			if (errorMessage != null) {
		%>
			<p style="color: red;"><%=errorMessage%></p>
		<%
		}
		%>

		<!-- Category form -->
		<form action="<%=request.getContextPath()%>/CategoryController"
			method="post">
			<div>
				<label for="categoryName">Category Name:</label> <input type="text"
					name="categoryName" required />
			</div>
			<div>
				<label for="categoryDescription">Category Description:</label> <input
					type="text" name="categoryDescription" required />
			</div>
			<div>
				<input type="submit" value="Add Category" />
			</div>
		</form>

		<h3>Existing Categories</h3>
		<%
		@SuppressWarnings("unchecked")
		Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
				.getAttribute("categoryServiceMap");

		if (sessionCategoryServiceMap == null && sessionCategoryServiceMap.isEmpty()) {
		%>
		<p>No categories found. Please add a new category.</p>
		<%
		} else {
		%>
		<table>
			<tr>
				<th>Category ID</th>
				<th>Category Name</th>
				<th>Category Description</th>
				<th>Actions</th>
			</tr>
			<%
			for (Map.Entry<Category, List<Service>> entry : sessionCategoryServiceMap.entrySet()) {
				Category cat = entry.getKey();
			%>
			<tr>
				<td><%=cat.getId()%></td>
				<td><%=cat.getName()%></td>
				<td><%=cat.getDescription()%></td>
				<td>
					<div class="actions">
						<!-- Edit Button -->
						<form action="editService.jsp" method="get">
							<input type="hidden" name="categoryId" value="<%=cat.getId()%>" />
							<button type="submit" class="btn-edit">Edit</button>
						</form>

						<!-- Delete Button -->
						<form action="<%=request.getContextPath()%>/CategoryController/delete" method="post">
						    <input type="hidden" name="categoryId" value="<%=cat.getId()%>" />
						    <button type="submit" class="btn-delete">Delete</button>
						</form>
					</div>
				</td>
			</tr>
			<%
			}
			%>
		</table>
		<% 
		}
		%>
	</div>
</body>
</html>