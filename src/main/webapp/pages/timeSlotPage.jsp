<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, jakarta.servlet.http.HttpSession, java.util.LinkedHashMap" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
	<style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
        }
        .calendar-container {
            max-width: 600px;
            margin: 40px auto;
            padding: 20px;
            background-color: white;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .calendar-header {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
        }
        .timeslot-grid {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .timeslot-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 15px;
            border: 1px solid #ccc;
            border-radius: 5px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .timeslot-unavailable {
            background-color: #e0e0e0;
            border-color: #b0b0b0;
            color: #808080;
        }
        .timeslot-available {
            background-color: #d4edda;
            border-color: #c3e6cb;
            color: #155724;
        }
        .timeslot-info {
            flex: 1;
            font-size: 16px;
        }
        .timeslot-button {
            padding: 8px 12px;
            border: none;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }
        .timeslot-button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
    </style>
	<%
		// get all the data
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