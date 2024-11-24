<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.Map, java.util.List,model.category,model.service"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>JAD-Washy-Washy</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/navbar.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/homePage.css">
<link rel="icon"
	href="<%=request.getContextPath()%>/assets/icons/favicon.ico"
	type="image/x-icon">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/carousel.css">
</head>
<body>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp"%>
	</div>

	<!-- Aligned Carousel Section -->
	<div class="carousel-container">
		<div id="carouselExample" class="carousel slide"
			data-bs-ride="carousel">
			<!-- Indicators -->
			<div class="carousel-indicators">
				<button type="button" data-bs-target="#carouselExample"
					data-bs-slide-to="0" class="active" aria-current="true"
					aria-label="Slide 1"></button>
				<button type="button" data-bs-target="#carouselExample"
					data-bs-slide-to="1" aria-label="Slide 2"></button>
				<button type="button" data-bs-target="#carouselExample"
					data-bs-slide-to="2" aria-label="Slide 3"></button>
			</div>

			<!-- Carousel Images -->
			<div class="carousel-inner">
				<div class="carousel-item active">
					<img
						src="<%=request.getContextPath()%>/assets/images/cleaningImage.jpg"
						class="d-block w-100" alt="Cleaning Service 1">
				</div>
				<div class="carousel-item">
					<img
						src="<%=request.getContextPath()%>/assets/images/cleaningImage2.jpg"
						class="d-block w-100" alt="Cleaning Service 2">
				</div>
				<div class="carousel-item">
					<img
						src="<%=request.getContextPath()%>/assets/images/cleaningImage3.jpg"
						class="d-block w-100" alt="Cleaning Service 3">
				</div>
			</div>

			<!-- Navigation Buttons -->
			<button class="carousel-control-prev" type="button"
				data-bs-target="#carouselExample" data-bs-slide="prev">
				<span class="carousel-control-prev-icon" aria-hidden="true"></span>
				<span class="visually-hidden">Previous</span>
			</button>
			<button class="carousel-control-next" type="button"
				data-bs-target="#carouselExample" data-bs-slide="next">
				<span class="carousel-control-next-icon" aria-hidden="true"></span>
				<span class="visually-hidden">Next</span>
			</button>
		</div>
	</div>

	<!-- Categories Container -->
	<div class="categories-container">
		<%
		// Retrieve the category-service map from the session
		@SuppressWarnings("unchecked")
		Map<category, List<service>> sessionCategoryServiceMap = (Map<category, List<service>>) session
				.getAttribute("categoryServiceMap");

		if (sessionCategoryServiceMap != null && !sessionCategoryServiceMap.isEmpty()) {
			for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
				category cat = entry.getKey();
				String categoryName = (cat.getName() != null) ? cat.getName().trim() : "Unknown Category";
				String categoryDescription = (cat.getDescription() != null)
				? cat.getDescription().trim()
				: "No description available.";
				List<service> services = entry.getValue();
		%>
		<div class="category" id="category-<%=cat.getId()%>">
			<h2 class="category-title"><%=categoryName%></h2>
			<p class="category-description"><%=categoryDescription%></p>
			<div class="services">
				<%
				if (services != null && !services.isEmpty()) {
					for (service serv : services) {
						String serviceName = (serv.getName() != null) ? serv.getName().trim() : "Unknown Service";
						String serviceDescription = (serv.getDescription() != null)
						? serv.getDescription().trim()
						: "No description available.";
				%>
				<div class="service-card">
					<h3 class="service-title"><%=serviceName%></h3>
					<p class="service-description"><%=serviceDescription%></p>
					<p class="service-price">
						Price: $<%=serv.getPrice()%></p>
					<p class="service-duration">
						Duration:
						<%=serv.getDurationInHour()%>
						hour(s)
					</p>
					<form action="<%=request.getContextPath()%>/bookingPage"
						method="get">
						 <input type="hidden" name="selectedService" value="<%= serviceName %>" />
						<button type="submit" class="book-button">Book Now</button>
					</form>

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
