<canvas id="categoryChart" width="400" height="400"></canvas>
<script>
    // Fetch data from the JSP
    const categoryLabels = [];
    const categoryCounts = [];
    <c:forEach var="row" items="${categoryWiseServices}">
        categoryLabels.push("Category ${row.category_id}");
        categoryCounts.push(${row.total_services});
    </c:forEach>;

    // Render Pie Chart
    const ctx = document.getElementById('categoryChart').getContext('2d');
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: categoryLabels,
            datasets: [{
                label: 'Services by Category',
                data: categoryCounts,
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'],
            }]
        },
    });
</script>