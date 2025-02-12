<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- Booking Progress Component -->
<div class="progress-container">
	<h2>Booking Progress</h2>

	<%
	Integer currentStatusId = (Integer) session.getAttribute("statusId");
	if (currentStatusId == null || currentStatusId < 6 || currentStatusId > 8) {
		currentStatusId = 6; // Default to "Awaiting Service" if no valid status found
	}

	// Define the booking stages and find the current index
	String[] stages = { "Awaiting Service", "Cleaning in Progress", "Service Completed" };
	int currentStageIndex = currentStatusId - 6; // Map status_id (6,7,8) to array index (0,1,2)
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