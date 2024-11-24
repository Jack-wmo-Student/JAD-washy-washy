<canvas id="bookingChart" width="600" height="400"></canvas>
<script>
    const bookingDates = [];
    const bookingCounts = [];
    // Assume you fetched bookings by date in servlet and set it as "bookingsByDate"
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
                label: 'Bookings Over Time',
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
</script>