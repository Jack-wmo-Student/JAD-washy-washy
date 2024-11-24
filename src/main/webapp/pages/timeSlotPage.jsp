<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, jakarta.servlet.http.HttpSession, java.util.LinkedHashMap" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/timeSlotPage.css">
	<%
		// get all the data
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null) {
				// Handle invalid login
				request.setAttribute("error", "You must log in first.");
				request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
				return;
			}
		@SuppressWarnings("unchecked")
		Map<String, Object> timeslotAvailability = (Map<String, Object>) session.getAttribute("timeslot-availability");
		String bookingDate = (String) timeslotAvailability.get("booking_date");
	%>
</head>
<body>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp" %>
	</div>
	
	<!-- Timeslot container -->
	<div class="calendar-container">
        <div class="calendar-header">Select a Timeslot</div>
        <h2 style="text-align: center;"><%= bookingDate %></h2>
        
        <form action="timeSlotLogic" method="post">
            <div class="timeslot-grid">
                <%               
                    if (timeslotAvailability != null) {
                        for (Map.Entry<String, Object> entry : timeslotAvailability.entrySet()) {
                            String timeslot = entry.getKey();
                            if (!(entry.getValue() instanceof Integer)) continue;
                            int availabilityStatus = (int) entry.getValue();
                            
                            String cssClass = "";
                            String statusText = "";
                            boolean isEnabled = false;

                            // Handle CSS class, text, and button availability based on the status
                            if (availabilityStatus == 2) {
                                cssClass = "timeslot-available";
                                statusText = "Available";
                                isEnabled = true;
                            } else if (availabilityStatus == 1) {
                                cssClass = "timeslot-partial";
                                statusText = "Time insufficient";
                                isEnabled = false;
                            } else {
                                cssClass = "timeslot-unavailable";
                                statusText = "Unavailable";
                                isEnabled = false;
                            }
                %>
                <div class="timeslot-item <%= cssClass %>">
                    <div class="timeslot-info">
                        <%= timeslot %> : <%= statusText %>
                    </div>
                    <button 
                        type="submit" 
                        name="timeslot" 
                        value="<%= timeslot %>" 
                        class="timeslot-button" 
                        <%= isEnabled ? "" : "disabled" %>>
                        Add to cart +
                    </button>
                </div>
                <% 
                        }
                    } else { 
                %>
                <div>No timeslots available.</div>
                <% 
                    } 
                %>
            </div>
        </form>
    </div>
</body>
</html>