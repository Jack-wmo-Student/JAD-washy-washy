<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/memberManagement.css">
</head>
<body>
    <%
    if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        request.setAttribute("error", "You must log in first.");
        request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
        return;
    }

    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    }
    %>
    <div>
        <%@ include file="/component/navbar.jsp"%>
    </div>
    <!-- Sidebar -->
    <div class="sidebar">
        <%@ include file="../component/adminSidebar.jsp"%>
    </div>

    <div class="layout-wrapper">
        <!-- Main Content Area -->
        <div class="main-content">
            <!-- Content Container -->
            <div class="content-container">
                <div class="card">
                    <h1>User Management</h1>

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
                                    boolean isCurrentUser = user.getUserId() == currentUserId;
                        %>
                            <tr data-user-id="<%=user.getUserId()%>">
                                <td><%=user.getUserId()%></td>
                                <td><%=user.getUsername()%></td>
                                <td>
                                    <span class="status-badge <%=user.isIsAdmin() ? "status-admin" : "status-user"%>">
                                        <%=user.isIsAdmin() ? "Admin" : "User"%>
                                    </span>
                                </td>
                                <td>
                                    <span class="status-badge <%=user.isIsBlocked() ? "status-blocked" : "status-active"%>">
                                        <%=user.isIsBlocked() ? "Blocked" : "Active"%>
                                    </span>
                                </td>
                                <td>
                                    <button onclick="toggleBlockStatus(<%=user.getUserId()%>)"
                                            class="btn <%=user.isIsBlocked() ? "btn-unblock" : "btn-block"%>"
                                            <%=isCurrentUser ? "disabled" : ""%>
                                            <%=user.isIsAdmin() ? "disabled" : ""%>>
                                        <%=user.isIsBlocked() ? "Unblock" : "Block"%>
                                    </button>
                                    
                                    <button onclick="toggleAdminStatus(<%=user.getUserId()%>)"
                                            class="btn <%=user.isIsAdmin() ? "btn-demote" : "btn-promote"%>"
                                            <%=isCurrentUser ? "disabled" : ""%>
                                            <%=user.isIsBlocked() ? "disabled" : ""%>>
                                        <%=user.isIsAdmin() ? "Remove Admin" : "Make Admin"%>
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
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
    <script>
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

            fetch(`${pageContext.request.contextPath}/UserController`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `action=${action}&userId=${userId}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.message) {
                    showSuccess(data.message);
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

        <%if (request.getAttribute("error") != null) {%>
            showError('<%=request.getAttribute("error")%>');
        <%}%>
        
        <%if (request.getAttribute("success") != null) {%>
            showSuccess('<%=request.getAttribute("success")%>');
        <%}%>
    </script>
</body>
</html>