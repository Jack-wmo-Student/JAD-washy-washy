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
		Map<String, String> timeslotAvailability = (Map<String, String>) session.getAttribute("timeslot-availability");
		String bookingDate = (String) session.getAttribute("booking-date");
	%>
</head>

<body>
		
		<%
		System.out.println("\n=== TimeSlot Page Processing ===");
		System.out.println("Retrieved map from session: " + 
		    (timeslotAvailability != null ? timeslotAvailability.size() + " slots" : "null"));
		if (timeslotAvailability != null) {
		    timeslotAvailability.forEach((timeRange, availability) -> {
		        System.out.println(timeRange + " -> " + availability);
		    });
		}
		%>
	<script>
	    function logTimeSlotNavigation() {
	        console.log("Date being sent: " + document.querySelector('[name="date"]').value);
	        console.log("Service ID being sent: " + document.querySelector('[name="serviceId"]').value);
	    }
	</script>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp" %>
	</div>
	
	<!-- Timeslot container -->
	<div class="calendar-container">
        <div class="calendar-header">Select a Timeslot</div>
        <h2 style="text-align: center;"><%= bookingDate %></h2>
        
        <form action="<%=request.getContextPath()%>/TimeSlotController" method="post">
            <div class="timeslot-grid">
                <%               
                    if (timeslotAvailability != null && !timeslotAvailability.isEmpty()) {
                        for (Map.Entry<String, String> entry : timeslotAvailability.entrySet()) {
                            String timeslot = entry.getKey();
                            String timeslotIdOrStatus = entry.getValue();
                            
                            String cssClass = "";
                            String statusText = "";
                            boolean isEnabled = false;
                            String timeslotId = "";

                            // Handle CSS class, text, and button availability based on the status
                            if (!timeslotIdOrStatus.equals("unavailable")) {
                                cssClass = "timeslot-available";
                                statusText = "Available";
                                timeslotId = timeslotIdOrStatus; // this is timeslot id
                            }  else {
                                cssClass = "timeslot-unavailable";
                                statusText = "Unavailable";
                                isEnabled = false;
                            }
                %>
                <div class="timeslot-item <%= cssClass %>">
                    <div class="timeslot-info">
                        <%= timeslot %>
                        <span class="status-text">
                            <%= statusText %>
                        </span>
                    </div>
                    <button 
                        type="submit" 
                        name="timeslot" x1
                        value="<%= timeslot + "," + timeslotId %>"
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

