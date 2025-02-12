<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, utils.sessionUtils, MODEL.CLASS.Service" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Service</title>
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

    // Admin-only access
    if (!sessionUtils.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
        return;
    }

    // Retrieve service details from request parameters
    String serviceIdStr = request.getParameter("serviceId");
    String serviceName = request.getParameter("serviceName");
    String servicePrice = request.getParameter("servicePrice");
    String serviceDuration = request.getParameter("serviceDuration");
    String serviceDescription = request.getParameter("serviceDescription");
    String serviceImage = request.getParameter("serviceImage");
    String categoryId = request.getParameter("categoryId");
    %>

    <div class="container">
        <h2>Edit Service</h2>

        <!-- Display error message if any -->
        <%     
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
        %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <%
            session.removeAttribute("errorMessage");
        }  
        %>

        <!-- Display success message if any -->
        <%     
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
        %>
            <div class="alert alert-success"><%= successMessage %></div>
        <%
            session.removeAttribute("successMessage");
        }  
        %>
        
        <form action="<%= request.getContextPath() %>/ServiceController/update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="serviceId" value="<%= serviceIdStr %>" />
            <input type="hidden" name="categoryId" value="<%= categoryId %>" />
            <input type="hidden" name="currentImageUrl" value="<%= serviceImage %>" />
            
            <div class="form-group">
                <label for="serviceName">Service Name:</label>
                <input type="text" name="serviceName" value="<%= serviceName %>" required />
            </div>
            
            <div class="form-group">
                <label for="servicePrice">Service Price:</label>
                <input type="number" name="servicePrice" step="0.01" value="<%= servicePrice %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceDuration">Service Duration (in hours):</label>
                <input type="number" name="serviceDuration" value="<%= serviceDuration %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceDescription">Service Description:</label>
                <input type="text" name="serviceDescription" value="<%= serviceDescription %>" required />
            </div>
            
            <div class="form-group">
                <label for="serviceImage">Service Image:</label>
                <% if (serviceImage != null && !serviceImage.trim().isEmpty()) { %>
                    <div class="image-container">
                        <img src="<%= serviceImage %>" width="100" alt="Current Image" />
                        <p>Current Image</p>
                    </div>
                <% } %>
                <div id="previewContainer" style="display: none;">
                    <img id="imagePreview" src="#" alt="Preview" style="max-width: 100px; margin-top: 10px;"/>
                    <p>Preview</p>
                </div>
                <input type="file" name="serviceImage" accept="image/*" 
                       onchange="if(validateImage(this)) previewImage(this)" />
                <p class="help-text">Leave empty to keep current image</p>
            </div>
            
            <div class="form-actions">
                <input type="submit" value="Update Service" class="btn btn-primary" />
                <a href="<%= request.getContextPath() %>/pages/editService.jsp?categoryId=<%= categoryId %>" 
                   class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>