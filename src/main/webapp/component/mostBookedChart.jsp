<canvas id="mostBookedChart" width="600" height="400"></canvas>
<script>
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
                label: 'Most Booked Services',
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