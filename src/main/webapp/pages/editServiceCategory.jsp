<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*, backend.category "%>
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
<title>Create Category</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
	<div class="container">
		<h2>Create Service Category</h2>


		<!-- Display success or error messages -->
		<%
        	
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (successMessage != null) {
        %>
		<p style="color: green;"><%= successMessage %></p>
		<%
            }
            if (errorMessage != null) {
        %>
		<p style="color: red;"><%= errorMessage %></p>
		<%
            }
        %>

		<!-- Category form -->
		<form action="<%=request.getContextPath()%>/CreateCategoryServlet"
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
    List<category> categories = (List<category>) request.getAttribute("categories");
    if (categories == null || categories.isEmpty()) {
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
			<% for (category category : categories) { %>
			<tr>
				<td><%= category.getId() %></td>
				<td><%= category.getName() %></td>
				<td><%= category.getDescription() %></td>
				<td>
					<div class="actions">
						<form action="editService.jsp" method="get"
							style="display: inline;">
							<input type="hidden" name="categoryId"
								value="<%= category.getId() %>" />
							<button type="submit" class="btn-edit">Edit</button>
						</form>
						<form action="deleteServiceCategory.jsp" method="get"
							style="display: inline;"
							onsubmit="return confirm('Are you sure you want to delete this category?');">
							<input type="hidden" name="categoryId"
								value="<%= category.getId() %>" />
							<button type="submit" class="btn-delete">Delete</button>
						</form>
					</div>
				</td>
			</tr>
			<% } %>
		</table>
		<%
    }
%>
	</div>
</body>
</html>