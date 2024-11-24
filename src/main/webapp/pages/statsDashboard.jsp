<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Business Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
</head>
<body>
<div class="container mt-5">
<%@ include file="../component/adminSidebar.jsp"%>
    <h1>Business Dashboard</h1>
    <hr>

    <!-- Total Services -->
    <h3>Total Services: ${totalServices}</h3>

    <!-- Category-wise Services Chart -->
    <h3>Services by Category</h3>
    <canvas id="categoryChart" width="400" height="400"></canvas>

    <!-- Bookings Over Time Chart -->
    <h3>Bookings Over Time</h3>
    <canvas id="bookingChart" width="600" height="400"></canvas>

    <!-- Most Booked Services Chart -->
    <h3>Most Booked Services</h3>
    <canvas id="mostBookedChart" width="600" height="400"></canvas>
</div>

<script>
    // Category-wise Services Chart
    const categoryLabels = [];
    const categoryCounts = [];
    <c:forEach var="row" items="${categoryWiseServices}">
        categoryLabels.push("Category ${row.category_id}");
        categoryCounts.push(${row.total_services});
    </c:forEach>;

    const ctx1 = document.getElementById('categoryChart').getContext('2d');
    new Chart(ctx1, {
        type: 'pie',
        data: {
            labels: categoryLabels,
            datasets: [{
                data: categoryCounts,
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'],
            }]
        }
    });

    // Bookings Over Time Chart
    const bookingDates = [];
    const bookingCounts = [];
    <c:forEach var="row" items="${bookingsByDate}">
        bookingDates.push("${row.booked_date}");
        bookingCounts.push(${row.total_bookings});
    </c:forEach>;

    const ctx2 = document.getElementById('bookingChart').getContext('2d');
    new Chart(ctx2, {
        type: 'line',
        data: {
            labels: bookingDates,
            datasets: [{
                data: bookingCounts,
                borderColor: '#36A2EB',
                fill: false,
            }]
        },
        options: {
            scales: {
                x: { title: { display: true, text: 'Date' } },
                y: { title: { display: true, text: 'Number of Bookings' } }
            }
        }
    });

    // Most Booked Services Chart
    const serviceLabels = [];
    const serviceBookings = [];
    <c:forEach var="row" items="${mostBookedServices}">
        serviceLabels.push("Service ${row.service_id}");
        serviceBookings.push(${row.total_bookings});
    </c:forEach>;

    const ctx3 = document.getElementById('mostBookedChart').getContext('2d');
    new Chart(ctx3, {
        type: 'bar',
        data: {
            labels: serviceLabels,
            datasets: [{
                data: serviceBookings,
                backgroundColor: '#4BC0C0',
            }]
        },
        options: {
            scales: {
                x: { title: { display: true, text: 'Service ID' } },
                y: { title: { display: true, text: 'Number of Bookings' } }
            }
        }
    });
</script>
</body>
</html>