<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,MODEL.CLASS.Service,MODEL.CLASS.Category, utils.sessionUtils"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Service for <%=matchingCategory.getName()%></title>
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
    if (requestCategoryId == null || requestCategoryId.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
        return;
    }

    @SuppressWarnings("unchecked")
    Map<Category, List<Service>> sessionCategoryServiceMap = 
        (Map<Category, List<Service>>) session.getAttribute("categoryServiceMap");

    int categoryId = Integer.parseInt(requestCategoryId);
    Category matchingCategory = null;

    for (Category category : sessionCategoryServiceMap.keySet()) {
        if (category.getId() == categoryId) {
            matchingCategory = category;
            break;
        }
    }

    if (matchingCategory == null) {
        response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
        return;
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
        <form action="<%=request.getContextPath()%>/ServiceController/create" method="post">
            <div>
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" placeholder="Enter service name" required />
            </div>
            <div>
                <label for="servicePrice">Service Price:</label>
                <input type="number" step="0.01" name="servicePrice" placeholder="Enter service price" required />
            </div>
            <div>
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" placeholder="Enter duration" required />
            </div>
            <div>
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" placeholder="Enter description" required />
            </div>
            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
            <div>
                <input type="submit" value="Add Service" />
            </div>
        </form>

        <!-- Services List -->
        <h3>Available Services for <%=matchingCategory.getName()%></h3>
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
                List<Service> serviceList = sessionCategoryServiceMap.get(matchingCategory);
                for (Service service : serviceList) {
                %>
                <tr>
                    <td><%=service.getId()%></td>
                    <td><%=service.getName()%></td>
                    <td><%=service.getPrice()%></td>
                    <td><%=service.getDurationInHour()%></td>
                    <td><%=service.getDescription()%></td>
                    <td>
                        <form action="serviceEditor.jsp" method="get" style="display: inline;">
                            <input type="hidden" name="serviceId" value="<%=service.getId()%>" />
                            <input type="hidden" name="serviceName" value="<%=service.getName()%>" />
                            <input type="hidden" name="servicePrice" value="<%=service.getPrice()%>" />
                            <input type="hidden" name="serviceDuration" value="<%=service.getDurationInHour()%>" />
                            <input type="hidden" name="serviceDescription" value="<%=service.getDescription()%>" />
                            <button type="submit" class="btn btn-edit">Edit</button>
                        </form>
                        <form action="<%=request.getContextPath()%>/ServiceController/delete" method="post">
                            <input type="hidden" name="serviceId" value="<%=service.getId()%>" />
                            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
                            <button type="submit" onclick="return confirm('Are you sure you want to delete this service?');">Delete Service</button>
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