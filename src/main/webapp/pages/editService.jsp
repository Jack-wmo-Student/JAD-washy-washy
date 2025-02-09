<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,MODEL.CLASS.Service,MODEL.CLASS.Category, utils.sessionUtils"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Service for Category</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
</head>
<body>

    <%
    // Authentication checks
    if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        request.setAttribute("error", "You must log in first.");
        request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
        return;
    }

    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    }

    // Fetching category details
    String requestCategoryId = request.getParameter("categoryId");
    System.out.println(requestCategoryId);

    @SuppressWarnings("unchecked")
    Map<Category, List<Service>> sessionCategoryServiceMap = 
        (Map<Category, List<Service>>) session.getAttribute("categoryServiceMap");

    int categoryId = Integer.parseInt(requestCategoryId);
    Category matchingCategory = null;
    List<Service> serviceList = null;

    for (Category category : sessionCategoryServiceMap.keySet()) {
        if (category.getId() == categoryId) {
            matchingCategory = category;
            serviceList = sessionCategoryServiceMap.get(category); // Add this line to get the service list
            break;
        }
    }
    %>

    <%@ include file="../component/adminSidebar.jsp"%>

    <div class="container">
        <h2>Create Service for Category: <%=matchingCategory.getName()%></h2>

        <!-- Success/Error Messages -->
        <%
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        if (successMessage != null) {
        %>
            <p class="success-message"><%=successMessage%></p>
        <% 
            session.removeAttribute("successMessage");
        } 
        if (errorMessage != null) {
        %>
            <p class="error-message"><%=errorMessage%></p>
        <%
            session.removeAttribute("errorMessage");
        }
        %>

        <!-- Service Creation Form -->
        <form action="<%=request.getContextPath()%>/ServiceController/create" method="post" enctype="multipart/form-data">
            <!-- Form remains the same -->
        </form>

        <!-- Services List -->
        <h3>Available Services for <%=matchingCategory.getName()%></h3>
        <table>
            <thead>
                <tr>
                    <th>Image</th>
                    <th>Service Name</th>
                    <th>Price</th>
                    <th>Duration (Hours)</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% 
                if (serviceList != null && !serviceList.isEmpty()) {
                    for (Service service : serviceList) { 
                %>
                <tr>
                    <td>
                        <% if (service.getImageUrl() != null) { %>
                            <img src="<%= request.getContextPath() + "/" + service.getImageUrl() %>" width="100" />
                        <% } else { %>
                            <p>No Image</p>
                        <% } %>
                    </td>
                    <td><%=service.getName()%></td>
                    <td><%=service.getPrice()%></td>
                    <td><%=service.getDurationInHour()%></td>
                    <td><%=service.getDescription()%></td>
                    <td>
                        <form action="<%=request.getContextPath()%>/pages/serviceEditor.jsp" method="get">
                            <input type="hidden" name="serviceId" value="<%=service.getId()%>" />
                            <button type="submit">Edit</button>
                        </form>
                        <form action="<%=request.getContextPath()%>/ServiceController/delete" method="post">
                            <input type="hidden" name="serviceId" value="<%=service.getId()%>" />
                            <button type="submit" onclick="return confirm('Delete this service?');">Delete</button>
                        </form>
                    </td>
                </tr>
                <% 
                    } 
                } else {
                %>
                <tr>
                    <td colspan="6">No services available for this category</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>