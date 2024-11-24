<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sidebar Navigation</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
        }
        .sidebar {
            height: 100vh; /* Full height */
            width: 250px; /* Sidebar width */
            position: fixed; /* Stay in place */
            top: 0;
            left: 0;
            background-color: #526D82; /* Dark background */
            padding-top: 70px;
            overflow-y: auto; /* Enable vertical scroll if needed */
        }
        .sidebar a {
            padding: 10px 15px;
            text-decoration: none;
            font-size: 18px;
            color: #fff; /* White text */
            display: block; /* Make links block elements */
        }
        .sidebar a:hover {
            background-color: #575757; /* Add a hover effect */
        }
        .content {
            margin-left: 260px; /* Same as the width of the sidebar */
            padding: 20px;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <a href="<%=request.getContextPath()%>/pages/editServiceCategory.jsp">Edit Categories/Services</a>
        <a href="<%=request.getContextPath()%>/pages/memberManagement.jsp">Manage Members</a>
        <a href="<%=request.getContextPath()%>/StatisticsServlet">Statistics Dashboard</a>
    </div>
</body>
</html>