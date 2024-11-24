<%@page import="java.util.List"%>
<%@page import="model.user"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Member List</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/memberManagement.css">
    <script>
        // Embed user data into JavaScript
        <%Map<String, List<user>> userStatusMap = (Map<String, List<user>>) session.getAttribute("userStatusMap"); %>
        const userMap = {
            adminUsers: <%
                List<user> adminUsers = userStatusMap.get("adminUsers");
                if (adminUsers != null) {
                    out.print("[");
                    for (int i = 0; i < adminUsers.size(); i++) {
                        user u = adminUsers.get(i);
                        out.print("{id:" + u.getId() + ", username:'" + u.getUsername() + "', isAdmin:" + u.isAdmin() + ", isBlocked:" + u.isBlocked() + "}");
                        if (i < adminUsers.size() - 1) {
                            out.print(",");
                        }
                    }
                    out.print("]");
                } else {
                    out.print("[]");
                }
            %>,
            blockedUsers: <%
                List<user> blockedUsers = userStatusMap.get("blockedUsers");
                if (blockedUsers != null) {
                    out.print("[");
                    for (int i = 0; i < blockedUsers.size(); i++) {
                        user u = blockedUsers.get(i);
                        out.print("{id:" + u.getId() + ", username:'" + u.getUsername() + "', isAdmin:" + u.isAdmin() + ", isBlocked:" + u.isBlocked() + "}");
                        if (i < blockedUsers.size() - 1) {
                            out.print(",");
                        }
                    }
                    out.print("]");
                } else {
                    out.print("[]");
                }
            %>,
            regularUsers: <%
                List<user> regularUsers = userStatusMap.get("regularUsers");
                if (regularUsers != null) {
                    out.print("[");
                    for (int i = 0; i < regularUsers.size(); i++) {
                        user u = regularUsers.get(i);
                        out.print("{id:" + u.getId() + ", username:'" + u.getUsername() + "', isAdmin:" + u.isAdmin() + ", isBlocked:" + u.isBlocked() + "}");
                        if (i < regularUsers.size() - 1) {
                            out.print(",");
                        }
                    }
                    out.print("]");
                } else {
                    out.print("[]");
                }
            %>
        };

        // Function to update the table based on selected user type
        function updateTable(userType) {
            const tableBody = document.getElementById('userTableBody');
            tableBody.innerHTML = ""; // Clear existing rows

            const users = userMap[userType];
            if (users && users.length > 0) {
                users.forEach(user => {
                    const row = `<tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.isAdmin}</td>
                        <td>${user.isBlocked}</td>
                        <td>
                            <form action="memberManagement.jsp" method="get" style="display: inline;">
                                <button type="submit" class="btn btn-edit">Edit</button>
                            </form>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });
            } else {
                tableBody.innerHTML = "<tr><td colspan='5'>No users found for this category.</td></tr>";
            }
        }
    </script>
</head>
<body>
    <div class="container">
        <h1>Member List</h1>

        <!-- Dropdown to select user type -->
        <label for="userType">Select User Type:</label>
        <select id="userType" onchange="updateTable(this.value)">
            <option value="adminUsers">Admin Users</option>
            <option value="blockedUsers">Blocked Users</option>
            <option value="regularUsers">Regular Users</option>
        </select>

        <!-- Table to display user list -->
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Admin</th>
                    <th>Blocked</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody id="userTableBody">
                <tr><td colspan="5">Select a user type to display the list.</td></tr>
            </tbody>
        </table>
    </div>
</body>
</html>