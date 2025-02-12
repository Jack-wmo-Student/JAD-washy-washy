<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page
    import="java.util.Map, java.util.List,MODEL.CLASS.Category,MODEL.CLASS.Service"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JAD-Washy-Washy</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/homePage.css">
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/carousel.css">
	<style>
		.service-card {
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            padding: 0;
            margin: 15px;
            width: 300px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            background: white;
        }

        .service-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }

        .service-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        .service-content {
            padding: 20px;
        }

        .service-title {
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 10px;
            color: #333;
        }

        .service-description {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 15px;
            line-height: 1.4;
        }

        .service-price {
            font-size: 1.1rem;
            color: #2c5282;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .service-duration {
            font-size: 0.9rem;
            color: #4a5568;
            margin-bottom: 15px;
        }

        .book-button {
            width: 100%;
            padding: 10px;
            background-color: #4299e1;
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: 500;
            cursor: pointer;
            transition: background-color 0.2s ease;
        }

        .book-button:hover {
            background-color: #3182ce;
        }

        .services {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 20px;
            padding: 20px 0;
        }
        
	</style>
</head>
<body>
    <!-- Include the Navbar -->
    <div>
        <%@ include file="/component/navbar.jsp"%>
    </div>

    <!-- Carousel Section (unchanged) -->
    <!-- ... your existing carousel code ... -->

    <!-- Categories Container -->
    <div class="categories-container">
        <%
        @SuppressWarnings("unchecked")
        Map<Category, List<Service>> sessionCategoryServiceMap = (Map<Category, List<Service>>) session
                .getAttribute("categoryServiceMap");

        if (sessionCategoryServiceMap != null && !sessionCategoryServiceMap.isEmpty()) {
            for (Map.Entry<Category, List<Service>> entry : sessionCategoryServiceMap.entrySet()) {
                Category cat = entry.getKey();
                String categoryName = (cat.getName() != null) ? cat.getName().trim() : "Unknown Category";
                String categoryDescription = (cat.getDescription() != null)
                ? cat.getDescription().trim()
                : "No description available.";
                List<Service> services = entry.getValue();
        %>
        <div class="category" id="category-<%=cat.getId()%>">
            <h2 class="category-title"><%=categoryName%></h2>
            <p class="category-description"><%=categoryDescription%></p>
            <div class="services">
                <%
                if (services != null && !services.isEmpty()) {
                    for (Service serv : services) {
                        String serviceName = (serv.getName() != null) ? serv.getName().trim() : "Unknown Service";
                        String serviceDescription = (serv.getDescription() != null)
                        ? serv.getDescription().trim()
                        : "No description available.";
                        String imageUrl = (serv.getImageUrl() != null && !serv.getImageUrl().trim().isEmpty())
                        ? serv.getImageUrl()
                        : request.getContextPath() + "/assets/images/default-service.jpg";
                %>
                <div class="service-card">
                    <img src="<%=imageUrl%>" alt="<%=serviceName%>" class="service-image" 
                         onerror="this.src='<%=request.getContextPath()%>/assets/images/default-service.jpg'">
                    <div class="service-content">
                        <h3 class="service-title"><%=serviceName%></h3>
                        <p class="service-description"><%=serviceDescription%></p>
                        <p class="service-price">$<%=String.format("%.2f", serv.getPrice())%></p>
                        <p class="service-duration">Duration: <%=serv.getDurationInHour()%> hour(s)</p>
                        <form action="<%=request.getContextPath()%>/bookingPage" method="get">
                            <input type="hidden" name="selectedService" value="<%=serviceName%>" />
                            <button type="submit" class="book-button">Book Now</button>
                        </form>
                    </div>
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
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
	<script>
		// Function to navigate to the selected category
		function navigateToCategory(categoryId) {
			if (categoryId) {
				const categoryElement = document.querySelector(categoryId);
				if (categoryElement) {
					const navbarHeight = document.querySelector(".navbar").offsetHeight;
					const position = categoryElement.offsetTop - navbarHeight
							- 10; // Add extra buffer space

					// Scroll to the adjusted position
					window.scrollTo({
						top : position,
						behavior : "smooth",
					});
				}
			}
		}
	</script>
</body>
</html>