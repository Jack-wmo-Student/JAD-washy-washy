<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error Occurred</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            padding: 20px;
            box-sizing: border-box;
        }
        .error-container {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 500px;
            width: 100%;
            text-align: center;
        }
        .error-icon {
            font-size: 60px;
            color: #ff6b6b;
            margin-bottom: 20px;
        }
        .error-title {
            color: #333;
            margin-bottom: 15px;
        }
        .error-message {
            color: #666;
            margin-bottom: 20px;
            word-wrap: break-word;
        }
        .error-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }
        .btn-primary {
            background-color: #3498db;
            color: white;
        }
        .btn-secondary {
            background-color: #95a5a6;
            color: white;
        }
        .btn:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <%
    // Retrieve error details
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        errorMessage = "An unexpected error occurred.";
    }

    // Log the error for administrators (you could integrate with a proper logging framework)
    System.err.println("Error occurred: " + errorMessage);
    %>

    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-title">Oops! Something Went Wrong</h1>
        
        <div class="error-message">
            <p><%= errorMessage %></p>
            
            <% 
            // If an exception was thrown, you might want to show more details in development
            if (exception != null) { 
            %>
                <details>
                    <summary>Technical Details</summary>
                    <p><%= exception.getMessage() %></p>
                </details>
            <% } %>
        </div>
        
        <div class="error-actions">
            <a href="<%= request.getContextPath() %>/pages/homePage.jsp" class="btn btn-primary">Go to Home</a>
            <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
        </div>
    </div>
</body>
</html>