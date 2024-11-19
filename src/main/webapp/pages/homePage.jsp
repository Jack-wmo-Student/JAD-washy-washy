<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, java.util.List, backend.category, backend.service" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JAD-Washy-Washy</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/homePage.css">
</head>
<body>
    <div><%@ include file="navbar.jsp" %></div>

    <div class="categories-container">
        <% 
            // Retrieve the category-service map from the session
            Map<category, List<service>> sessionCategoryServiceMap = 
                (Map<category, List<service>>) session.getAttribute("categoryServiceMap");

            if (sessionCategoryServiceMap != null && !sessionCategoryServiceMap.isEmpty()) {
                for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
                    category cat = entry.getKey();
                    List<service> services = entry.getValue();
        %>
        <div class="category" id="category-<%= cat.getId() %>">
            <h2 class="category-title"><%= cat.getName() %></h2>
            <p class="category-description"><%= cat.getDescription() %></p>
            <div class="services">
                <% 
                    if (services != null && !services.isEmpty()) {
                        for (service serv : services) {
                %>
                <div class="service-card">
                    <h3 class="service-title"><%= serv.getName() %></h3>
                    <p class="service-description"><%= serv.getDescription() %></p>
                    <p class="service-price">Price: $<%= serv.getPrice() %></p>
                    <p class="service-duration">Duration: <%= serv.getDurationInHour() %> hour(s)</p>
                    <button class="book-button">Book Now</button>
                </div>
                <% 
                        }
                    } else {
                %>
                <p>No services available under this category.</p>
                <% 
                    }
                %>
            </div>
        </div>
        <% 
                }
            } else { 
        %>
        <p>No categories available.</p>
        <% 
            } 
        %>
    </div>
</body>
</html>
