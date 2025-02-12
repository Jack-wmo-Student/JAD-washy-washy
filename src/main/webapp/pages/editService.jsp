<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,MODEL.CLASS.Service,MODEL.CLASS.Category, utils.sessionUtils"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Service for Category</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/serviceManagement.css">
    
    <script>
        function previewImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('imagePreview').src = e.target.result;
                    document.getElementById('previewContainer').style.display = 'block';
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        function validateImage(input) {
            const file = input.files[0];
            const maxSize = 5 * 1024 * 1024; // 5MB
            
            if (file) {
                if (file.size > maxSize) {
                    alert('File is too large. Maximum size is 5MB.');
                    input.value = '';
                    return false;
                }
                
                if (!file.type.match('image.*')) {
                    alert('Only image files are allowed.');
                    input.value = '';
                    return false;
                }
            }
            return true;
        }
    </script>
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
            serviceList = sessionCategoryServiceMap.get(category);
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
            <div class="form-group">
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" required />
            </div>
            <div class="form-group">
                <label for="servicePrice">Service Price:</label>
                <input type="number" step="0.01" name="servicePrice" required />
            </div>
            <div class="form-group">
                <label for="serviceDuration">Duration (in hours):</label>
                <input type="number" name="serviceDuration" required />
            </div>
            <div class="form-group">
                <label for="serviceDescription">Description:</label>
                <input type="text" name="serviceDescription" required />
            </div>
            <div class="form-group">
                <label for="serviceImage">Upload Image:</label>
                <div id="previewContainer" style="display: none;">
                    <img id="imagePreview" src="#" alt="Preview" style="max-width: 100px; margin-top: 10px;"/>
                    <p>Preview</p>
                </div>
                <input type="file" name="serviceImage" accept="image/*" 
                       onchange="if(validateImage(this)) previewImage(this)" required />
            </div>
            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
            <div class="form-actions">
                <input type="submit" value="Add Service" class="btn btn-primary" />
            </div>
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
                    <td class="image-container">
                        <% if (service.getImageUrl() != null && !service.getImageUrl().trim().isEmpty()) { %>
                            <img src="<%= service.getImageUrl() %>" width="100" alt="<%= service.getName() %>" />
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
                            <input type="hidden" name="serviceName" value="<%=service.getName()%>" />
                            <input type="hidden" name="servicePrice" value="<%=service.getPrice()%>" />
                            <input type="hidden" name="serviceDuration" value="<%=service.getDurationInHour()%>" />
                            <input type="hidden" name="serviceDescription" value="<%=service.getDescription()%>" />
                            <input type="hidden" name="serviceImage" value="<%=service.getImageUrl()%>" />
                            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
                            <button type="submit" class="btn btn-edit">Edit</button>
                        </form>
                        <form action="<%=request.getContextPath()%>/ServiceController/delete" method="post">
                            <input type="hidden" name="serviceId" value="<%=service.getId()%>" />
                            <input type="hidden" name="categoryId" value="<%=categoryId%>" />
                            <button type="submit" class="btn btn-delete" 
                                    onclick="return confirm('Delete this service?');">Delete</button>
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