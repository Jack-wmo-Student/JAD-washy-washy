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
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
</head>
<body>
    <!-- Include the Navbar -->
    <div><%@ include file="/component/navbar.jsp" %></div>

    <!-- Carousel Section -->
    <div class="carousel">
        <div class="carousel-images">
            <img src="<%=request.getContextPath()%>/assets/images/cleaningImage.jpg" alt="Cleaning Service 1">
            <img src="<%=request.getContextPath()%>/assets/images/cleaningImage2.jpg" alt="Cleaning Service 2">
            <img src="<%=request.getContextPath()%>/assets/images/cleaningImage3.jpg" alt="Cleaning Service 3">
        </div>
        <button class="carousel-arrow left" id="prevArrow">&#10094;</button>
        <button class="carousel-arrow right" id="nextArrow">&#10095;</button>
    </div>

    <!-- Categories Container -->
    <div class="categories-container">
        <% 
            // Retrieve the category-service map from the session
            Map<category, List<service>> sessionCategoryServiceMap = 
                (Map<category, List<service>>) session.getAttribute("categoryServiceMap");

            if (sessionCategoryServiceMap != null && !sessionCategoryServiceMap.isEmpty()) {
                for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
                    category cat = entry.getKey();
                    String categoryName = (cat.getName() != null) ? cat.getName().trim() : "Unknown Category";
                    String categoryDescription = (cat.getDescription() != null) ? cat.getDescription().trim() : "No description available.";
                    List<service> services = entry.getValue();
        %>
        <div class="category" id="category-<%= cat.getId() %>">
            <h2 class="category-title"><%= categoryName %></h2>
            <p class="category-description"><%= categoryDescription %></p>
            <div class="services">
                <% 
                    if (services != null && !services.isEmpty()) {
                        for (service serv : services) {
                            String serviceName = (serv.getName() != null) ? serv.getName().trim() : "Unknown Service";
                            String serviceDescription = (serv.getDescription() != null) ? serv.getDescription().trim() : "No description available.";
                %>
                <div class="service-card">
                    <h3 class="service-title"><%= serviceName %></h3>
                    <p class="service-description"><%= serviceDescription %></p>
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
        <script src="<%=request.getContextPath()%>/assets/carousel.js"></script> <!-- Carousel-specific JS -->
</body>
</html>
