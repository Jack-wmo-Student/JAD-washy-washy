<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*,model.category, model.service"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>Create Category</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>
	<div class="container">
		<%@ include file="../component/adminSidebar.jsp"%>
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
		Map<category, List<service>> sessionCategoryServiceMap = (Map<category, List<service>>) session
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
			for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
				category cat = entry.getKey();
			%>
			<tr>
				<td><%=cat.getId()%></td>
				<td><%=cat.getName()%></td>
				<td><%=cat.getDescription()%></td>
				<td>
					<div class="actions">
						<!-- Edit Button -->
						<form action="editService.jsp" method="get"
							style="display: inline;">
							<input type="hidden" name="categoryId" value="<%=cat.getId()%>" />
							<button type="submit" class="btn-edit">Edit</button>
						</form>

						<!-- Delete Button -->
						<form action="<%=request.getContextPath()%>/DeleteCategoryServlet"
							method="post">
							<input type="hidden" name="categoryId" value="<%=cat.getId()%>" />
							<button type="submit">Delete Category</button>
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