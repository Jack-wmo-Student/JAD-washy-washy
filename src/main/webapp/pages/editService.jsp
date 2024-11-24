<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,model.service, model.category, utils.sessionUtils"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>Create Service</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>

	<% if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        request.setAttribute("error", "You must log in first.");
        request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
        return;
    }

    // Optional: Check if the user is an admin
    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    } %>

	<%@ include file="../component/adminSidebar.jsp"%>
	
	
	<div class="container">
		<%
		String requestCategoryId = request.getParameter("categoryId");
		Map<category, List<service>> sessionCategoryServiceMap = (Map<category, List<service>>) session
				.getAttribute("categoryServiceMap");

		int categoryId = Integer.parseInt(requestCategoryId);
		category matchingCategory = null;

		for (category category : sessionCategoryServiceMap.keySet()) {
			if (category.getId() == categoryId) {
				matchingCategory = category;
				break;
			}
		}
		%>

		<h2>
			Create Service for Category:
			<%=matchingCategory.getName()%></h2>

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

		<form action="<%=request.getContextPath()%>/ServiceServlet"
			method="post">

			<div>
				<label for="serviceName">Service Name:</label> <input type="text"
					name="serviceName" placeholder="Enter service name" required />
			</div>
			<div>
				<label for="servicePrice">Service Price:</label> <input
					type="number" name="servicePrice" placeholder="Enter service price"
					required />
			</div>
			<div>
				<label for="serviceDuration">Service Duration (in hours):</label> <input
					type="number" name="serviceDuration" placeholder="Enter duration"
					required />
			</div>
			<div>
				<label for="serviceDescription">Service Description:</label> <input
					type="text" name="serviceDescription"
					placeholder="Enter description" required />
			</div>
			<%System.out.print(categoryId + "FIRST");%>
			<input type="hidden" name="categoryId" value="<%=categoryId%>" />
			<div>
				<input type="submit" value="Add Service" />
			</div>
		</form>

		<h3>
			Available Services for
			<%=matchingCategory.getName()%></h3>
		<table>
			<thead>
				<tr>
					<th>Service ID</th>
					<th>Service Name</th>
					<th>Price</th>
					<th>Duration (Hours)</th>
					<th>Description</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<service> serviceList = sessionCategoryServiceMap.get(matchingCategory);
				for (service service : serviceList) {
				%>
				<tr>
					<td><%=service.getId()%></td>
					<td><%=service.getName()%></td>
					<td><%=service.getPrice()%></td>
					<td><%=service.getDurationInHour()%></td>
					<td><%=service.getDescription()%></td>
					<td>
						<form action="serviceEditor.jsp" method="get"
							style="display: inline;">
							<input type="hidden" name="serviceId"
								value="<%=service.getId()%>" /> <input type="hidden"
								name="serviceName" value="<%=service.getName()%>" /> <input
								type="hidden" name="servicePrice"
								value="<%=service.getPrice()%>" /> <input type="hidden"
								name="serviceDuration" value="<%=service.getDurationInHour()%>" />
							<input type="hidden" name="serviceDescription"
								value="<%=service.getDescription()%>" />

							<button type="submit" class="btn btn-edit">Edit</button>
						</form>
						<form action="<%=request.getContextPath()%>/DeleteServiceServlet"
							method="post">
							<input type="hidden" name="serviceId"
								value="<%=service.getId()%>" /> <input type="hidden"
								name="categoryId" value="<%=categoryId%>" />
							<button type="submit">Delete Service</button>
						</form>
					</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
	</div>
</body>
</html>