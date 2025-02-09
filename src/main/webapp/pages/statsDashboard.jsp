<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
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
</html> --%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*,MODEL.CLASS.ServiceStatistics,utils.sessionUtils"%>
<%@ page import="java.text.DecimalFormat"%>

<!DOCTYPE html>
<html>
<head>
    <title>Service Statistics</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
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

    // Set default timeframe if not present
    String timeframe = (String) request.getAttribute("timeframe");
    if (timeframe == null) {
        timeframe = "this month";
    }
    DecimalFormat df = new DecimalFormat("#,##0.00");
    %>
    
    <div>
        <%@ include file="/component/navbar.jsp"%>
    </div>
    
    <div class="sidebar">
        <%@ include file="../component/adminSidebar.jsp"%>
    </div>
  

    <div class="container mt-4">
        <h1>Service Statistics</h1>

        <!-- Timeframe selector -->
        <div class="mb-4">
            <form action="<%=request.getContextPath()%>/statistics" method="GET" class="row g-3">
                <div class="col-auto">
                    <select name="timeframe" class="form-select" onchange="this.form.submit()">
                        <option value="this month" <%=timeframe.equals("this month") ? "selected" : ""%>>This Month</option>
                        <option value="last month" <%=timeframe.equals("last month") ? "selected" : ""%>>Last Month</option>
                        <option value="last 3 months" <%=timeframe.equals("last 3 months") ? "selected" : ""%>>Last 3 Months</option>
                    </select>
                </div>
            </form>
        </div>

        <!-- Error/Success Messages -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <div class="row">
            <!-- Most Profitable Services -->
            <div class="col-md-6 mb-4">
                <div class="card">
                    <div class="card-header">
                        <h3>Most Profitable Services</h3>
                    </div>
                    <div class="card-body">
                        <canvas id="profitableServicesChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- Least Profitable Services -->
            <div class="col-md-6 mb-4">
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

        <!-- Bookings Per Service -->
        <div class="row">
            <div class="col-12 mb-4">
                <div class="card">
                    <div class="card-header">
                        <h3>Bookings Per Service</h3>
                    </div>
                    <div class="card-body">
                        <canvas id="bookingsChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Detailed Statistics Tables -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h3>Detailed Statistics</h3>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Service Name</th>
                                    <th>Total Revenue</th>
                                    <th>Total Bookings</th>
                                    <th>Profit per Booking</th>
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
        // Prepare data for charts
        <% 
        @SuppressWarnings("unchecked")
        List<ServiceStatistics> mostProfitable = (List<ServiceStatistics>) request.getAttribute("mostProfitableServices");
        @SuppressWarnings("unchecked")
        List<ServiceStatistics> leastProfitable = (List<ServiceStatistics>) request.getAttribute("leastProfitableServices");
        %>

        // Most Profitable Services Chart
        new Chart(document.getElementById('profitableServicesChart'), {
            type: 'bar',
            data: {
                labels: [
                    <% if (mostProfitable != null) {
                        for (ServiceStatistics stat : mostProfitable) { %>
                            '<%=stat.getServiceName()%>',
                    <% }
                    } %>
                ],
                datasets: [{
                    label: 'Revenue ($)',
                    data: [
                        <% if (mostProfitable != null) {
                            for (ServiceStatistics stat : mostProfitable) { %>
                                <%=stat.getTotalRevenue()%>,
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
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Revenue ($)'
                        }
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
                            '<%=stat.getServiceName()%>',
                    <% }
                    } %>
                ],
                datasets: [{
                    label: 'Revenue ($)',
                    data: [
                        <% if (leastProfitable != null) {
                            for (ServiceStatistics stat : leastProfitable) { %>
                                <%=stat.getTotalRevenue()%>,
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
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Revenue ($)'
                        }
                    }
                }
            }
        });

        // Bookings Per Service Chart
        new Chart(document.getElementById('bookingsChart'), {
            type: 'line',
            data: {
                labels: [
                    <% if (bookingsPerService != null) {
                        for (ServiceStatistics stat : bookingsPerService) { %>
                            '<%=stat.getServiceName()%>',
                    <% }
                    } %>
                ],
                datasets: [{
                    label: 'Number of Bookings',
                    data: [
                        <% if (bookingsPerService != null) {
                            for (ServiceStatistics stat : bookingsPerService) { %>
                                <%=stat.getTotalBookings()%>,
                        <% }
                        } %>
                    ],
                    borderColor: 'rgba(54, 162, 235, 1)',
                    tension: 0.1,
                    fill: false
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Number of Bookings'
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>