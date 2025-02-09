<!-- bookingProgress.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Booking Progress</title>
</head>
<body>
	<div
		class="progress-container ${bookingStatus eq 'Cancelled' ? 'cancelled' : ''}">
		<h2>Booking Progress</h2>

		<%
		String currentStatus = (String) request.getAttribute("bookingStatus");
		String[] stages = {"Awaiting Service", "Cleaning in Progress", "Service Completed", "Pending Payment",
				"Transaction Completed"};
		int currentStageIndex = -1;

		// Find current stage index
		for (int i = 0; i < stages.length; i++) {
			if (stages[i].equals(currentStatus)) {
				currentStageIndex = i;
				break;
			}
		}

		// Calculate progress width for CSS variable
		double progressWidth = currentStageIndex == -1 ? 0 : (currentStageIndex / (double) (stages.length - 1)) * 100;
		%>

		<div class="progress-tracker"
			style="--progress-width: <%=progressWidth%>%">
			<%
			for (int i = 0; i < stages.length; i++) {
			%>
			<div class="progress-step">
				<div
					class="step-icon <%=i <= currentStageIndex ? "completed" : ""%> 
                                        <%=i == currentStageIndex ? "current" : ""%>">
					<%
					if (i <= currentStageIndex) {
					%>
					&#10003;
					<!-- Checkmark -->
					<%
					} else {
					%>
					<%=i + 1%>
					<%
					}
					%>
				</div>
				<div class="step-label"><%=stages[i]%></div>
			</div>
			<%
			}
			%>
		</div>

		<%
		if ("Cancelled".equals(currentStatus)) {
		%>
		<div class="cancelled-message"
			style="color: #f44336; text-align: center; margin-top: 20px;">
			<strong>This booking has been cancelled</strong>
		</div>
		<%
		}
		%>
	</div>

	<!-- Optional: Add auto-refresh for status updates -->
	<script>
		// Refresh the page every 30 seconds to check for status updates
		setTimeout(function() {
			window.location.reload();
		}, 30000);
	</script>
</body>
</html>