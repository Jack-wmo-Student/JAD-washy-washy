<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, MODEL.CLASS.User, utils.sessionUtils"%>
<%@ page import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7fc;
            margin: 0;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
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

        .btn {
            padding: 8px 15px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .btn-block {
            background-color: #e74c3c;
            color: white;
        }

        .btn-unblock {
            background-color: #2ecc71;
            color: white;
        }

        .btn-promote {
            background-color: #f39c12;
            color: white;
        }

        .btn-demote {
            background-color: #8e44ad;
            color: white;
        }

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
</head>
<body>
    <% if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) { %>
        <c:redirect url="/pages/login.jsp">
            <c:param name="error" value="You must log in first."/>
        </c:redirect>
    <% } %>

    <% if (!sessionUtils.isAdmin(request)) { %>
        <c:redirect url="/pages/forbidden.jsp"/>
    <% } %>

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
</body>
</html>