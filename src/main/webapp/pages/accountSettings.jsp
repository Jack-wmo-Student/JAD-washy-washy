<%@ page import="MODEL.CLASS.User"%>
<%@ page import="java.util.List"%>
<%@ page import="MODEL.DAO.TransactionHistoryDAO"%>
<%@ page import="MODEL.CLASS.TransactionHistory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Account Settings</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/accountSettings.css">
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
    <!-- Transaction table styles -->
    <style>
        .transaction-section {
            margin-top: 2rem;
            padding: 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .transaction-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            font-size: 0.9rem;
        }

        .transaction-table th,
        .transaction-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }

        .transaction-table th {
            background-color: #f5f5f5;
            font-weight: 600;
        }

        .transaction-table tr:hover {
            background-color: #f8f8f8;
        }

        .status-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .status-completed { background: #e8f5e9; color: #2e7d32; }
        .status-pending { background: #fff3e0; color: #ef6c00; }
        .status-cancelled { background: #ffebee; color: #c62828; }

        .no-transactions {
            text-align: center;
            padding: 20px;
            color: #666;
        }
    </style>
</head>
<body>
    <%@include file="/component/navbar.jsp"%>
    <%@include file="/component/bookingProgress.jsp"%>
    <%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }
    User user = (User) session.getAttribute("currentUser");
    String username = user.getUsername();
    %>

    <!-- Account Settings Form -->
    <form id="settingsForm" class="settings-form">
        <h2>Account Settings</h2>

        <%-- Display error message if exists --%>
        <%
        String error = request.getParameter("error");
        if (error != null) {
        %>
        <p class="error" style="color: red; font-weight: bold;"><%=error%></p>
        <% } %>

        <!-- Username -->
        <label for="username">Username</label>
        <input type="text" id="username" name="username" value="<%=username%>" required>

        <!-- Password -->
        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Enter a new password">

        <!-- Confirm Password -->
        <label for="confirm-password">Confirm Password</label>
        <input type="password" id="confirm-password" name="confirmPassword" 
               placeholder="Confirm your new password">

        <!-- Save Changes Button -->
        <button type="submit">Update Details</button>
    </form>

    <!-- Transaction History Section -->
    <div class="transaction-section">
        <h2>Transaction History</h2>
        <%
        TransactionHistoryDAO transactionDAO = new TransactionHistoryDAO();
        List<TransactionHistory> transactions = transactionDAO.getUserTransactions(user.getUserId());
        
        if (transactions != null && !transactions.isEmpty()) {
        %>
        <table class="transaction-table">
            <thead>
                <tr>
                    <th>Booking ID</th>
                    <th>Service</th>
                    <th>Date</th>
                    <th>Amount</th>
                    <th>Payment Method</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% for(TransactionHistory transaction : transactions) { %>
                <tr>
                    <td>#<%= transaction.getBookingId() %></td>
                    <td><%= transaction.getServiceName() %></td>
                    <td><%= new java.text.SimpleDateFormat("dd MMM yyyy").format(transaction.getBookedDate()) %></td>
                    <td>$<%= String.format("%.2f", transaction.getPrice()) %></td>
                    <td><%= transaction.getPaymentMethod() %></td>
                    <td>
                        <span class="status-badge status-<%= transaction.getBookingStatus().toLowerCase() %>">
                            <%= transaction.getBookingStatus() %>
                        </span>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } else { %>
        <p class="no-transactions">No transaction history available.</p>
        <% } %>
    </div>

    <!-- Form submission script -->
    <script>
    document.getElementById("settingsForm").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent normal form submission

        const formData = new FormData(this);
        const jsonData = JSON.stringify(Object.fromEntries(formData)); // Convert form data to JSON

        fetch('<%=request.getContextPath()%>/UserController', {
            method: 'PUT',
            body: jsonData,
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                alert("Unexpected response from server.");
            }
        })
        .catch(error => {
            console.error("Request Failed:", error);
            alert("Error updating user details. Please try again.");
        });
    });
    </script>
</body>
</html>