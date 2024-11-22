<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard Sidebar</title>
    <style>
        /* Basic styling for sidebar */
        body {
            margin: 0;
            font-family: Arial, sans-serif;
        }
        .sidebar {
            width: 250px;
            height: 100vh;
            background-color: #333;
            color: #fff;
            display: flex;
            flex-direction: column;
            padding: 20px;
        }
        .sidebar h2 {
            margin: 0 0 20px;
            font-size: 20px;
            text-align: center;
        }
        .sidebar a {
            color: #fff;
            text-decoration: none;
            padding: 10px;
            margin: 5px 0;
            text-align: center;
            border-radius: 5px;
            background-color: #444;
        }
        .sidebar a:hover {
            background-color: #555;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>Admin Dashboards</h2>
        <a href="services.jsp">Services</a>
        <a href="members.jsp">Members</a>
        <a href="stats.jsp">Stats</a>
    </div>
</body>
</html>
