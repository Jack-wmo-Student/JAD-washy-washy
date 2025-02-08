<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*,MODEL.CLASS.User,MODEL.CLASS.Category,utils.sessionUtils"%>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/memberManagement.css">
</head>
<body>
    <%
    // Security checks
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
    
    <div class="sidebar">
        <%@ include file="../component/adminSidebar.jsp"%>
    </div>

    <div class="layout-wrapper">
        <div class="main-content">
            <div class="content-container">
                <div class="card">
                    <h1>User Management</h1>

                    <!-- Display any error or success messages -->
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
                    <% if (request.getAttribute("success") != null) { %>
                        <div class="alert alert-success">
                            <%= request.getAttribute("success") %>
                        </div>
                    <% } %>

                    <table id="usersTable" class="table">
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
                        List<User> users = (List<User>)request.getAttribute("users");
                        if (users != null) {
                            for (User user : users) {
                                User currentUser = (User)session.getAttribute("currentUser");
                                int currentUserId = currentUser.getUserId();
                                boolean isCurrentUser = user.getUserId() == currentUserId;
                        %>
                            <tr>
                                <td><%=user.getUserId()%></td>
                                <td><%=user.getUsername()%></td>
                                <td>
                                    <span class="badge <%= user.isIsAdmin() ? "bg-primary" : "bg-secondary" %>">
                                        <%= user.isIsAdmin() ? "Admin" : "User" %>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge <%= user.isIsBlocked() ? "bg-danger" : "bg-success" %>">
                                        <%= user.isIsBlocked() ? "Blocked" : "Active" %>
                                    </span>
                                </td>
                                <td>
                                    <!-- Block/Unblock Form -->
                                    <form action="<%=request.getContextPath()%>/UserController" method="POST" style="display: inline;">
                                        <input type="hidden" name="action" value="toggle-block">
                                        <input type="hidden" name="userId" value="<%=user.getUserId()%>">
                                        <button type="submit" 
                                                class="btn btn-sm <%= user.isIsBlocked() ? "btn-success" : "btn-danger" %>"
                                                <%= isCurrentUser || user.isIsAdmin() ? "disabled" : "" %>>
                                            <%= user.isIsBlocked() ? "Unblock" : "Block" %>
                                        </button>
                                    </form>

                                    <!-- Admin Status Form -->
                                    <form action="<%=request.getContextPath()%>/UserController" method="POST" style="display: inline;">
                                        <input type="hidden" name="action" value="toggle-admin">
                                        <input type="hidden" name="userId" value="<%=user.getUserId()%>">
                                        <button type="submit" 
                                                class="btn btn-sm <%= user.isIsAdmin() ? "btn-warning" : "btn-info" %>"
                                                <%= isCurrentUser || user.isIsBlocked() ? "disabled" : "" %>>
                                            <%= user.isIsAdmin() ? "Remove Admin" : "Make Admin" %>
                                        </button>
                                    </form>
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
</body>
</html>