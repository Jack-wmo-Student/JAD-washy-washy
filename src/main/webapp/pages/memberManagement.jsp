<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>


<html>
<head>
<title>User Management</title>
<style>
body {
	font-family: Arial, sans-serif;
	background-color: #f4f7fc;
	margin: 0;
	padding: 20px;
}

h1 {
	color: #333;
	text-align: center;
	margin-bottom: 30px;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin: 0 auto;
	background-color: #fff;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	border-radius: 8px;
}

th, td {
	padding: 12px 15px;
	text-align: left;
}

th {
	background-color: #3498db;
	color: white;
	font-weight: bold;
}

td {
	border-bottom: 1px solid #ddd;
}

tr:hover {
	background-color: #f9f9f9;
}

button {
	padding: 8px 15px;
	margin: 5px;
	border: none;
	border-radius: 5px;
	font-size: 14px;
	cursor: pointer;
}

button:hover {
	opacity: 0.8;
}

.block-button {
	background-color: #e74c3c;
	color: white;
}

.unblock-button {
	background-color: #2ecc71;
	color: white;
}

.promote-button {
	background-color: #f39c12;
	color: white;
}

.demote-button {
	background-color: #8e44ad;
	color: white;
}

.actions-form {
	display: inline;
}
</style>
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

	<h1>User Management</h1>
	<%@ include file="../component/navbar.jsp"%>
	<%@ include file="../component/adminSidebar.jsp"%>
	<table>
		<thead>
			<tr>
				<th>User ID</th>
				<th>Username</th>
				<th>Is Admin</th>
				<th>Is Blocked</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<%
			@SuppressWarnings("unchecked")
			ArrayList<User> users = (ArrayList<User>) request.getAttribute("users");
			for (User user : users) {
			%>
			<tr>
				<td><%=user.getUserId()%></td>
				<td><%=user.getUsername()%></td>
				<td><%=user.isIsAdmin() ? "Yes" : "No"%></td>
				<td><%=user.isIsBlocked() ? "Yes" : "No"%></td>
				<td>
					<!-- Block/Unblock Form -->
					<form action="BlockUserServlet" method="post" class="actions-form">
						<input type="hidden" name="userId" value="<%=user.getUserId()%>">
						<button type="submit"
							class="<%=user.isIsBlocked() ? "unblock-button" : "block-button"%>">
							<%=user.isIsBlocked() ? "Unblock" : "Block"%>
						</button>
					</form> <!-- Promote/Demote Form -->
					<form action="PromoteUserServlet" method="post"
						class="actions-form">
						<input type="hidden" name="userId" value="<%=user.getUserId()%>">
						<button type="submit"
							class="<%=user.isIsAdmin() ? "demote-button" : "promote-button"%>">
							<%=user.isIsAdmin() ? "Demote from Admin" : "Promote to Admin"%>
						</button>
					</form>
				</td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
</body>
</html>