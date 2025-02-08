<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Business Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/statsDashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	
    <%
    	int totalServices = (int) request.getAttribute("totalServices");
    	int totalBookings = (int) request.getAttribute("totalBookings");
    	@SuppressWarnings("unchecked")
    	List<Map<String, Object>> categoryWiseServices = (List<Map<String, Object>>) request.getAttribute("categoryWiseServices");
    	@SuppressWarnings("unchecked")
    	List<Map<String, Object>> mostBookedServices = (List<Map<String, Object>>) request.getAttribute("mostBookedServices");
    	@SuppressWarnings("unchecked")
    	List<Map<String, Object>> bookingsByDate = (List<Map<String, Object>>) request.getAttribute("bookingsByDate");
    %>
</head>
<body>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp"%>
	</div>
	
	<!--  Side bar -->
	<div>
   		<%@ include file="../component/adminSidebar.jsp" %>
   	</div>
   	
	<div class="container mt-1">
		
	    <h1>Business Dashboard</h1>
	    <hr>
	
	    <!-- Dashboard stats section -->
	    <div class="dashboard-stats">
	        <div class="stat-box">
	            <h3>${totalServices}</h3>
	            <p>Total Services</p>
	        </div>
	        <div class="stat-box">
	            <h3>${totalBookings}</h3>
	            <p>Total Bookings</p>
	        </div>
	    </div>
	
	    <!-- Chart sections -->
	    <div class="chart-container">
	        <h3>Services by Category</h3>
	        <canvas id="categoryChart"></canvas>
	    </div>
	
	    <div class="chart-container">
	        <h3>Bookings Over Time</h3>
	        <canvas id="bookingChart"></canvas>
	    </div>
	
	    <div class="chart-container">
	        <h3>Most Booked Services</h3>
	        <canvas id="mostBookedChart"></canvas>
	    </div>
	</div>
	
	<!-- <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>  -->
	<script>
	    // Prepare data for the charts
	    const categoryLabels = [
	        <% for (Map<String, Object> row : categoryWiseServices) { %>
	            "Category <%= row.get("category_id") %>",
	        <% } %>
	    ];
	    const categoryCounts = [
	        <% for (Map<String, Object> row : categoryWiseServices) { %>
	            <%= row.get("total_services") %>,
	        <% } %>
	    ];
	
	    const bookingDates = [
	        <% for (Map<String, Object> row : bookingsByDate) { %>
	            "<%= row.get("booked_date") %>",
	        <% } %>
	    ];
	    const bookingCounts = [
	        <% for (Map<String, Object> row : bookingsByDate) { %>
	            <%= row.get("total_bookings") %>,
	        <% } %>
	    ];
	
	    const serviceLabels = [
	        <% for (Map<String, Object> row : mostBookedServices) { %>
	            "Service <%= row.get("service_id") %>",
	        <% } %>
	    ];
	    const serviceBookings = [
	        <% for (Map<String, Object> row : mostBookedServices) { %>
	            <%= row.get("total_bookings") %>,
	        <% } %>
	    ];
	
	    // Initialize charts
	    const ctx1 = document.getElementById('categoryChart').getContext('2d');
	    new Chart(ctx1, {
	        type: 'pie',
	        data: {
	            labels: categoryLabels,
	            datasets: [{
	                data: categoryCounts,
	                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
	            }]
	        }
	    });
	
	    const ctx2 = document.getElementById('bookingChart').getContext('2d');
	    new Chart(ctx2, {
	        type: 'line',
	        data: {
	            labels: bookingDates,
	            datasets: [{
	                data: bookingCounts,
	                borderColor: '#36A2EB',
	                fill: false
	            }]
	        },
	        options: {
	            scales: {
	                x: { title: { display: true, text: 'Date' } },
	                y: { title: { display: true, text: 'Number of Bookings' } }
	            },
	            plugins: {
	                legend: {
	                    display: false // Hide the legend
	                }
	            }
	        }
	    });
	
	    const ctx3 = document.getElementById('mostBookedChart').getContext('2d');
	    new Chart(ctx3, {
	        type: 'bar',
	        data: {
	            labels: serviceLabels,
	            datasets: [{
	                data: serviceBookings,
	                backgroundColor: '#4BC0C0'
	            }]
	        },
	        options: {
	            scales: {
	                x: { title: { display: true, text: 'Service ID' } },
	                y: { title: { display: true, text: 'Number of Bookings' } }
	            },
	            plugins: {
	                legend: {
	                    display: false
	                }
	            }
	        }
	    });
	</script>
</body>
</html>