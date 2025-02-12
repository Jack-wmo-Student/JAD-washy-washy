<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.*,MODEL.CLASS.ServiceStatistics,utils.sessionUtils"%>
<%@ page import="java.text.DecimalFormat"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Business Dashboard</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/navbar.css">
<%-- <link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/statsDashboard.css"> --%>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
	<%
	// Security checks
	if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
		request.setAttribute("error", "You must log in first.");
		request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
		return;
	}

	if (!sessionUtils.isAdmin(request)) {
		response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
		return;
	}

	String timeframe = (String) request.getAttribute("timeframe");
	if (timeframe == null)
		timeframe = "this month";

	DecimalFormat df = new DecimalFormat("#,##0.00");
	%>

	<div>
		<%@ include file="/component/navbar.jsp"%>
	</div>
	
	<div class=sidebar>
		<%@ include file="../component/adminSidebar.jsp"%>
	</div>
	<div class="layout-wrapper">
		

		<div class="container mt-4">
			<h1>Business Dashboard</h1>

			<!-- Timeframe selector -->
			<div class="mb-4">
				<form action="<%=request.getContextPath()%>/statistics" method="GET"
					class="row g-3">
					<div class="col-auto">
						<select name="timeframe" class="form-select"
							onchange="this.form.submit()">
							<option value="this month"
								<%="this month".equals(timeframe) ? "selected" : ""%>>This
								Month</option>
							<option value="last month"
								<%="last month".equals(timeframe) ? "selected" : ""%>>Last
								Month</option>
							<option value="last 3 months"
								<%="last 3 months".equals(timeframe) ? "selected" : ""%>>Last
								3 Months</option>
						</select>
					</div>
				</form>
			</div>

			<!-- Dashboard stats section -->
			<div class="container-fluid p-0">
				<div class="row mb-4">
					<div class="col-md-3">
						<div class="card">
							<div class="card-body">
								<h5 class="card-title">Total Services</h5>
								<h2 class="card-text">${totalServices}</h2>
							</div>
						</div>
					</div>
					<div class="col-md-3">
						<div class="card">
							<div class="card-body">
								<h5 class="card-title">Total Bookings</h5>
								<h2 class="card-text">${totalBookings}</h2>
							</div>
						</div>
					</div>
				</div>

				<!-- Legacy Charts -->
				<div class="row mb-4">
					<div class="col-md-6">
						<div class="card">
							<div class="card-header">
								<h3>Services by Category</h3>
							</div>
							<div class="card-body">
								<canvas id="categoryChart"></canvas>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card">
							<div class="card-header">
								<h3>Bookings Over Time</h3>
							</div>
							<div class="card-body">
								<canvas id="bookingChart"></canvas>
							</div>
						</div>
					</div>
				</div>

				<!-- New Statistics Charts -->
				<div class="row mb-4">
					<div class="col-md-6">
						<div class="card">
							<div class="card-header">
								<h3>Most Profitable Services</h3>
							</div>
							<div class="card-body">
								<canvas id="profitableServicesChart"></canvas>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card">
							<div class="card-header">
								<h3>Least Profitable Services</h3>
							</div>
							<div class="card-body">
								<canvas id="leastProfitableServicesChart"></canvas>
							</div>
						</div>
					</div>
				</div>

				<div class="row mb-4">
					<div class="col-12">
						<div class="card">
							<div class="card-header">
								<h3>Service Growth Trends</h3>
							</div>
							<div class="card-body">
								<canvas id="serviceTrendsChart"></canvas>
							</div>
						</div>
					</div>
				</div>

				<!-- Detailed Statistics Table -->
				<div class="card mb-4">
					<div class="card-header">
						<h3>Service Performance Details</h3>
					</div>
					<div class="card-body">
						<table class="table table-striped">
							<thead>
								<tr>
									<th>Service Name</th>
									<th>Total Revenue</th>
									<th>Total Bookings</th>
									<th>Average Revenue per Booking</th>
								</tr>
							</thead>
							<tbody>
								<%
							@SuppressWarnings("unchecked")
							List<ServiceStatistics> bookingsPerService = (List<ServiceStatistics>) request.getAttribute("bookingsPerService");
							if (bookingsPerService != null) {
								for (ServiceStatistics stat : bookingsPerService) {
							%>
								<tr>
									<td><%=stat.getServiceName()%></td>
									<td>$<%=df.format(stat.getTotalRevenue())%></td>
									<td><%=stat.getTotalBookings()%></td>
									<td>$<%=df.format(stat.getProfitMargin())%></td>
								</tr>
								<%
							}
							}
							%>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script>
	// Service Trends Chart
	const trendsChart = new Chart(document.getElementById('serviceTrendsChart'), {
	    type: 'bar',
	    data: {
	        labels: [
	            <% List<ServiceStatistics> trends = (List<ServiceStatistics>) request.getAttribute("serviceTrends");
	            if (trends != null) {
	                for (ServiceStatistics stat : trends) { %>
	                    "<%= stat.getServiceName() %>",
	                <% }
	            } %>
	        ],
	        datasets: [{
	            label: 'Current Month Revenue',
	            backgroundColor: 'rgba(75, 192, 192, 0.6)',
	            data: [
	                <% if (trends != null) {
	                    for (ServiceStatistics stat : trends) { %>
	                        <%= stat.getTotalRevenue() %>,
	                    <% }
	                } %>
	            ],
	            order: 2
	        }, {
	            label: 'Previous Month Revenue',
	            backgroundColor: 'rgba(192, 75, 75, 0.6)',
	            data: [
	                <% if (trends != null) {
	                    for (ServiceStatistics stat : trends) { %>
	                        <%= stat.getPreviousRevenue() %>,
	                    <% }
	                } %>
	            ],
	            order: 2
	        }, {
	            label: 'Revenue Growth (%)',
	            type: 'line',
	            borderColor: 'rgb(54, 162, 235)',
	            borderWidth: 2,
	            fill: false,
	            data: [
	                <% if (trends != null) {
	                    for (ServiceStatistics stat : trends) { %>
	                        <%= stat.getRevenueGrowth() %>,
	                    <% }
	                } %>
	            ],
	            yAxisID: 'growth',
	            order: 1
	        }]
	    },
	    options: {
	        responsive: true,
	        scales: {
	            y: {
	                beginAtZero: true,
	                title: {
	                    display: true,
	                    text: 'Revenue ($)'
	                }
	            },
	            growth: {
	                position: 'right',
	                beginAtZero: true,
	                title: {
	                    display: true,
	                    text: 'Growth Rate (%)'
	                }
	            }
	        },
	        plugins: {
	            title: {
	                display: true,
	                text: 'Month-over-Month Service Performance'
	            },
	            tooltip: {
	                callbacks: {
	                    label: function(context) {
	                        if (context.datasetIndex === 2) {
	                            return `Growth: ${context.raw.toFixed(1)}%`;
	                        }
	                        return `Revenue: $${context.raw.toFixed(2)}`;
	                    }
	                }
	            }
	        }
	    }
	});
	</script>

	<script>
        // Legacy Chart Data
	const categoryLabels = [
	    <% 
	    @SuppressWarnings("unchecked")
	    List<Map<String, Object>> categoryWiseServices = 
	        (List<Map<String, Object>>) request.getAttribute("categoryWiseServices");
	    if (categoryWiseServices != null) {
	        for (Map<String, Object> row : categoryWiseServices) { 
	    %>
	        "Category <%= row.get("category_id") %>",
	    <% 
	        }
	    } 
	    %>
	];
        const categoryCounts = [
            <%if (categoryWiseServices != null) {
				for (Map<String, Object> row : categoryWiseServices) {%>
                <%=row.get("total_services")%>,
            <%}
}%>
        ];

        // Initialize Legacy Charts
        new Chart(document.getElementById('categoryChart'), {
            type: 'pie',
            data: {
                labels: categoryLabels,
                datasets: [{
                    data: categoryCounts,
                    backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
                }]
            }
        });

        //=======================
        	
        // New Statistics Charts
<% 
@SuppressWarnings("unchecked")
List<ServiceStatistics> mostProfitable = 
    (List<ServiceStatistics>) request.getAttribute("mostProfitableServices");
@SuppressWarnings("unchecked")
List<ServiceStatistics> leastProfitable = 
    (List<ServiceStatistics>) request.getAttribute("leastProfitableServices");
%>

// Most Profitable Services Chart
new Chart(document.getElementById('profitableServicesChart'), {
    type: 'bar',
    data: {
        labels: [
            <% if (mostProfitable != null) {
                for (ServiceStatistics stat : mostProfitable) { %>
                    "<%= stat.getServiceName() %>",
                <% }
            } %>
        ],
        datasets: [{
            label: 'Revenue ($)',
            data: [
                <% if (mostProfitable != null) {
                    for (ServiceStatistics stat : mostProfitable) { %>
                        <%= stat.getTotalRevenue() %>,
                    <% }
                } %>
            ],
            backgroundColor: 'rgba(75, 192, 192, 0.6)'
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

// Least Profitable Services Chart
new Chart(document.getElementById('leastProfitableServicesChart'), {
    type: 'bar',
    data: {
        labels: [
            <% if (leastProfitable != null) {
                for (ServiceStatistics stat : leastProfitable) { %>
                    "<%= stat.getServiceName() %>",
                <% }
            } %>
        ],
        datasets: [{
            label: 'Revenue ($)',
            data: [
                <% if (leastProfitable != null) {
                    for (ServiceStatistics stat : leastProfitable) { %>
                        <%= stat.getTotalRevenue() %>,
                    <% }
                } %>
            ],
            backgroundColor: 'rgba(255, 99, 132, 0.6)'
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});
        // Bookings Over Time Chart
       	const bookingDates = [
		    <% 
		    @SuppressWarnings("unchecked")
		    List<Map<String, Object>> bookingsByDate = 
		        (List<Map<String, Object>>) request.getAttribute("bookingsByDate");
		    if (bookingsByDate != null) {
		        for (Map<String, Object> row : bookingsByDate) { 
		    %>
		        "<%= row.get("booked_date") %>",
		    <% 
		        }
		    } 
		    %>
		];
       	const bookingCounts = [
       	    <% 
       	    if (bookingsByDate != null) {
       	        for (Map<String, Object> row : bookingsByDate) { 
       	    %>
       	        <%= row.get("total_bookings") %>,
       	    <% 
       	        }
       	    } 
       	    %>
       	];

        new Chart(document.getElementById('bookingChart'), {
            type: 'line',
            data: {
                labels: bookingDates,
                datasets: [{
                    label: 'Number of Bookings',
                    data: bookingCounts,
                    borderColor: '#36A2EB',
                    fill: false
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    </script>
</body>
</html>