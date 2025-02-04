<<<<<<< HEAD
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>
=======
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, MODEL.CLASS.User, utils.sessionUtils"%>
<%@ page import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<!DOCTYPE html>
<html>
<head>
<<<<<<< HEAD
<title>User Management</title>
<style>
body {
	font-family: Arial, sans-serif;
	background-color: #f4f7fc;
	margin: 0;
	padding: 20px;
}
=======
    <title>User Management</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7fc;
            margin: 0;
            padding: 20px;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
h1 {
	color: #333;
	text-align: center;
	margin-bottom: 30px;
}
=======
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

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

<<<<<<< HEAD
button {
	padding: 8px 15px;
	margin: 5px;
	border: none;
	border-radius: 5px;
	font-size: 14px;
	cursor: pointer;
}
=======
        .btn {
            padding: 8px 15px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
button:hover {
	opacity: 0.8;
}
=======
        .btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
.block-button {
	background-color: #e74c3c;
	color: white;
}
=======
        .btn-block {
            background-color: #e74c3c;
            color: white;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
.unblock-button {
	background-color: #2ecc71;
	color: white;
}
=======
        .btn-unblock {
            background-color: #2ecc71;
            color: white;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
.promote-button {
	background-color: #f39c12;
	color: white;
}
=======
        .btn-promote {
            background-color: #f39c12;
            color: white;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
.demote-button {
	background-color: #8e44ad;
	color: white;
}
=======
        .btn-demote {
            background-color: #8e44ad;
            color: white;
        }
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
.actions-form {
	display: inline;
}
</style>
=======
        .loading {
            opacity: 0.5;
            pointer-events: none;
        }

        #errorMessage, #successMessage {
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
            display: none;
        }

        #errorMessage {
            background-color: #ffe6e6;
            color: #cc0000;
        }

        #successMessage {
            background-color: #e6ffe6;
            color: #006600;
        }
    </style>
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git
</head>
<body>
<<<<<<< HEAD
	<%
	if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
		request.setAttribute("error", "You must log in first.");
		request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
		return;
	}
=======
    <% if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) { %>
        <c:redirect url="/pages/login.jsp">
            <c:param name="error" value="You must log in first."/>
        </c:redirect>
    <% } %>
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
	// Optional: Check if the user is an admin
	if (!sessionUtils.isAdmin(request)) {
		response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
		return;
	}
	%>
=======
    <% if (!sessionUtils.isAdmin(request)) { %>
        <c:redirect url="/pages/forbidden.jsp"/>
    <% } %>
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git

<<<<<<< HEAD
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
=======
    <div class="container">
        <h1>User Management</h1>
        <%@ include file="../component/navbar.jsp"%>
        <%@ include file="../component/adminSidebar.jsp"%>

        <div id="errorMessage"></div>
        <div id="successMessage"></div>

        <table id="usersTable">
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Admin Status</th>
                    <th>Account Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <% 
                List<User> users = (List<User>) request.getAttribute("users");
                if (users != null) {
                    for (User user : users) {
                        int currentUserId = (int) session.getAttribute("userId");
                        boolean isCurrentUser = user.getUserId()== currentUserId;
            %>
                <tr data-user-id="<%= user.getUserId() %>">
                    <td><%= user.getUserId() %></td>
                    <td><%= user.getUsername() %></td>
                    <td><span class="admin-status"><%= user.isIsAdmin() ? "Admin" : "User" %></span></td>
                    <td><span class="block-status"><%= user.isIsBlocked() ? "Blocked" : "Active" %></span></td>
                    <td>
                        <button 
                            onclick="toggleBlockStatus(<%= user.getUserId() %>)"
                            class="btn <%= user.isIsBlocked() ? "btn-unblock" : "btn-block" %>"
                            <%= isCurrentUser ? "disabled" : "" %>
                            <%= user.isIsAdmin() ? "disabled" : "" %>>
                            <%= user.isIsBlocked() ? "Unblock" : "Block" %>
                        </button>
                        
                        <button 
                            onclick="toggleAdminStatus(<%= user.getUserId() %>)"
                            class="btn <%= user.isIsAdmin() ? "btn-demote" : "btn-promote" %>"
                            <%= isCurrentUser ? "disabled" : "" %>
                            <%= user.isIsBlocked() ? "disabled" : "" %>>
                            <%= user.isIsAdmin() ? "Remove Admin" : "Make Admin" %>
                        </button>
                    </td>
                </tr>
            <% 
                    }
                } 
            %>
            </tbody>
        </table>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
    <script>
        // Configure toastr
        toastr.options = {
            "closeButton": true,
            "progressBar": true,
            "positionClass": "toast-top-right",
            "timeOut": "3000"
        };

        function showError(message) {
            toastr.error(message);
        }

        function showSuccess(message) {
            toastr.success(message);
        }

        function toggleBlockStatus(userId) {
            sendUserAction(userId, 'toggle-block');
        }

        function toggleAdminStatus(userId) {
            sendUserAction(userId, 'toggle-admin');
        }

        function sendUserAction(userId, action) {
            const row = document.querySelector(`tr[data-user-id="${userId}"]`);
            row.classList.add('loading');

            fetch(`${pageContext.request.contextPath}/user/${action}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `userId=${userId}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.message) {
                    showSuccess(data.message);
                    // Refresh the page to show updated status
                    window.location.reload();
                }
            })
            .catch(error => {
                showError('An error occurred. Please try again.');
                console.error('Error:', error);
            })
            .finally(() => {
                row.classList.remove('loading');
            });
        }

        // Handle server-side messages if any
        <% if (request.getAttribute("error") != null) { %>
            showError('<%= request.getAttribute("error") %>');
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            showSuccess('<%= request.getAttribute("success") %>');
        <% } %>
    </script>
>>>>>>> branch 'main' of https://github.com/Giga-JAD/JAD-washy-washy-CA2.git
</body>
</html>