<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>403 Forbidden</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/forbidden.css">
</head>
<body>
    <div class="forbidden-container">
        <h1>403 Forbidden</h1>
        <p>Sorry, you don't have permission to access this page.</p>
        <a href="<%=request.getContextPath()%>/pages/homePage.jsp" class="back-link">Go Back Home</a>
    </div>
</body>
</html>
