<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, backend.category" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<%--     <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css"> --%>
    <title>JAD-Washy-Washy Navbar</title>
</head>
<body>
    <div class="navbar">
        <!-- Navbar Header -->
        <h2>JAD-Washy-Washy</h2>
        <ul class="nav-links">
            <!-- Home Link -->
            <li><a href="<%=request.getContextPath()%>/pages/homePage.jsp">Home</a></li>

            <!-- Admin Dashboard Link -->

            <li><a href="<%=request.getContextPath()%>/pages/editServiceCategory.jsp">Admin Dashboard</a></li>
            
            <!-- Categories Dropdown -->
            <li class="dropdown">
                <a href="#" class="dropdown-btn">Categories</a>
                <div class="dropdown-menu">
                    <% 
                    // Retrieve category-service map from the session
                    Map<category, List<category>> categoryServiceMap = 
                        (Map<category, List<category>>) session.getAttribute("categoryServiceMap");

                    // Dynamically render category links or show fallback message
                    if (categoryServiceMap != null && !categoryServiceMap.isEmpty()) {
                        for (category cat : categoryServiceMap.keySet()) {
                            String categoryName = (cat.getName() != null) ? cat.getName().trim() : "Unknown Category";
                    %>
                            <a href="#category-<%=cat.getId()%>"><%= categoryName %></a>
                    <% 
                        }
                    } else { 
                    %>
                        <p class="no-categories">No categories available.</p>
                    <% 
                    } 
                    %>
                </div>
            </li>
        </ul>

        <!-- Logout Button -->
        <form action="<%=request.getContextPath()%>/logout" method="POST" class="logout-form">
            <button type="submit" class="logout-button">Log Out</button>
        </form>
    </div>
</body>
</html>
