<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- Booking Progress Component -->
<div class="progress-container">
	<h2>Booking Progress</h2>

	<%
	Integer statusId = (Integer) request.getAttribute("statusId");
	if (statusId == null || statusId < 6 || statusId > 8) {
		statusId = 6; // Default to "Awaiting Service" if no status found
		request.setAttribute("statusId", statusId);
	}

	// Define the booking stages and find the current index
	String[] stages = { "Awaiting Service", "Cleaning in Progress", "Service Completed" };
	int currentStageIndex = statusId - 6; // Map status_id (6,7,8) to array index (0,1,2)
	%>

	<div class="progress-tracker">
		<%
		for (int i = 0; i < stages.length; i++) {
		%>
		<div class="progress-step">
			<div
				class="step-icon <%=i <= currentStageIndex ? "completed" : ""%> 
                                     <%=i == currentStageIndex ? "current" : ""%>">
				<%=(i <= currentStageIndex) ? "✔" : (i + 1)%>
			</div>
			<div class="step-label"><%=stages[i]%></div>
		</div>
		<%
		}
		%>
	</div>
</div>
