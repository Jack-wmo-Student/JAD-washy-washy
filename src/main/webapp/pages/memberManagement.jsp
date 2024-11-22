<%@page import="java.util.List"%>
<%@page import="model.user, java.util.*, "%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Member List</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/memberManagement.css">
</head>
<body>
    <div class="container">
        <h1>Member List</h1>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <div class="error-message">
                Error: <%= errorMessage %>
            </div>
        <%
            } else {
                @SuppressWarnings("unchecked");
                /* List<user> members = (List<userr>) request.getAttribute("members"); */

                if (members == null || members.isEmpty()) {
        %>
            <p>No members found.</p>
        <%
                } else {
        %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Admin</th>
                        <th>Blocked</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (user member : members) {
                    %>
                    <tr>
                        <td><%= member.getId() %></td>
                        <td><%= member.getUsername() %></td>
                        <td><%= member.isAdmin() %></td>
                        <td><%= member.isBlocked() %></td>
                        <td>
                            <form action="memberManagement.jsp" method="get" style="display: inline;">
                                <button type="submit" class="btn btn-edit">Edit</button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        <%
                }
            }
        %>
    </div>
</body>
</html>