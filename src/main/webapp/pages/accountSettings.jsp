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
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/navbar.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/bookingProgress.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/accountSettings.css">
<link rel="icon"
	href="<%=request.getContextPath()%>/assets/icons/favicon.ico"
	type="image/x-icon">
</head>
<body>
	<%@include file="/component/navbar.jsp"%>

	<%
	if (session == null || session.getAttribute("currentUser") == null) {
		response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
		return;
	}
	User user = (User) session.getAttribute("currentUser");
	String username = user.getUsername();
	%>

	<div class="container">
		<div class="row">
			<!-- Left Panel: Account Settings -->
			<div class="col-md-4">
				<div class="settings-form">
					<h2>Account Settings</h2>

					<%
					String error = request.getParameter("error");
					%>
					<%
					if (error != null) {
					%>
					<p class="error text-danger fw-bold"><%=error%></p>
					<%
					}
					%>

					<form id="settingsForm">
						<label for="username" class="form-label">Username</label> <input
							type="text" id="username" name="username" class="form-control"
							value="<%=username%>" required> <label for="password"
							class="form-label">Password</label> <input type="password"
							id="password" name="password" class="form-control"
							placeholder="Enter a new password"> <label
							for="confirm-password" class="form-label">Confirm
							Password</label> <input type="password" id="confirm-password"
							name="confirmPassword" class="form-control"
							placeholder="Confirm your new password">

						<button type="submit" class="btn btn-primary w-100 mt-3">Update
							Details</button>
					</form>
				</div>
			</div>

			<!-- Right Panel: Status & Transactions -->
			<div class="col-md-8">
				<div class="status-section mb-3 p-3 bg-white shadow-sm rounded">
					<%@include file="/component/bookingProgress.jsp"%>
					<%
					TransactionHistoryDAO transactionDAO = new TransactionHistoryDAO();
					List<TransactionHistory> transactions = transactionDAO.getUserTransactions(user.getUserId());

					boolean hasPendingAcknowledgment = transactions.stream()
							.anyMatch(t -> t.getBookingStatus().equalsIgnoreCase("Completed"));

					if (hasPendingAcknowledgment) {
					%>
					<p>Your booking has been completed.</p>
					<form action="<%=request.getContextPath()%>/AcknowledgeServlet"
						method="post">
						<input type="hidden" name="userId" value="<%=user.getUserId()%>">
						<button type="submit" id="acknowledgeBtn" class="btn btn-success">Acknowledge
							Completion</button>
					</form>
					<%
					} else {
					%>
					<p>No pending acknowledgements.</p>
					<%
					}
					%>
				</div>

				<div class="transaction-section p-3 bg-white shadow-sm rounded">
					<h3>Transaction History</h3>
					<%
					if (!transactions.isEmpty()) {
					%>
					<table class="table table-bordered transaction-table">
						<thead class="table-light">
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
							<%
							for (TransactionHistory transaction : transactions) {
							%>
							<tr>
								<td>#<%=transaction.getBookingId()%></td>
								<td><%=transaction.getServiceName()%></td>
								<td><%=new java.text.SimpleDateFormat("dd MMM yyyy").format(transaction.getBookedDate())%></td>
								<td>$<%=String.format("%.2f", transaction.getPrice())%></td>
								<td><%=transaction.getPaymentMethod()%></td>
								<td class="status-badge"><%=transaction.getBookingStatus()%></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
					<%
					} else {
					%>
					<p class="text-center text-muted">No transactions available.</p>
					<%
					}
					%>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap Bundle (for dropdowns, modals, etc.) -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

	<!-- Form Submission Script -->
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

    // Acknowledge Completion button click (Optional AJAX-based handling)
    document.getElementById("acknowledgeBtn")?.addEventListener("click", function(event) {
        event.preventDefault();

        fetch('<%=request.getContextPath()%>/AcknowledgeServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `userId=<%=user.getUserId()%>`
        })
        .then(response => {
            if (response.ok) {
                alert("Acknowledgement successful!");
                location.reload(); // Refresh page
            } else {
                alert("Error acknowledging completion.");
            }
        })
        .catch(error => {
            console.error("Request Failed:", error);
            alert("Network error, please try again.");
        });
    });
    </script>
</body>
</html>
