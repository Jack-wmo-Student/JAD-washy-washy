<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar, java.text.DateFormatSymbols" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Monthly Booking Calendar</title>
    <style>
        body {
            display: flex;
            font-family: Arial, sans-serif;
        }
        .sidebar {
            width: 20%;
            background-color: #f4f4f4;
            padding: 10px;
            box-shadow: 2px 0px 5px rgba(0, 0, 0, 0.1);
        }
        .calendar-container {
            width: 80%;
            padding: 20px;
        }
        .sidebar h3, .calendar-container h1 {
            text-align: center;
        }
        .month-year-select {
		    margin: 20px 0;
		    display: flex; 
		    align-items: center; 
		    justify-content: center; 
		    gap: 10px; 
		}
		#dropdown {
	        padding: 8px 10px;
	        margin: 15px 13px;
	        width: 90%;
	        border-radius: 5px;
	        border-color: #a3a3a3;
	    }
		.next-prev {
		    text-decoration: none; /* Remove underline */
		    font-size: 18px;
		    font-weight: bold;
		    color: #333; /* Neutral color */
		    padding: 5px 10px; /* Add some spacing */
		    border-radius: 4px; /* Rounded corners */
		    background-color: #f9f9f9; /* Subtle background */
		    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1); /* Soft shadow */
		    transition: all 0.2s ease-in-out; /* Add smooth hover effect */
		}
		
		.next-prev:hover {
		    background-color: #e0e0e0; /* Slightly darker on hover */
		    color: #000;
		    transform: scale(1.1); /* Slight zoom effect */
		}
		
		.current-month-year {
		    width: 170px; /* Adjust width to fit content */
		    padding: 5px 15px;
		    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
		    border-radius: 4px;
		    text-align: center;
		    background-color: #fff; /* White background */
		}
		
		.current-month-year span {
		    font-size: 20px;
		    font-weight: bold;
		    color: #444; /* Neutral text color */
		}

        .calendar {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            gap: 5px;
        }
        .header, .day {
            padding: 10px;
            border: 1px solid #ccc;
            text-align: center;
        }
        .empty {
            background-color: #f0f0f0;
        }
    </style>
	<%
	    // Get current month and year dynamically or from request parameters for navigation
	    Calendar calendar = Calendar.getInstance();
	    int currentMonth = calendar.get(Calendar.MONTH); // 0-based
	    int currentYear = calendar.get(Calendar.YEAR);
	    boolean isCurrentMonth = (currentMonth == Calendar.getInstance().get(Calendar.MONTH)) && (currentYear == Calendar.getInstance().get(Calendar.YEAR));
	
	    String paramMonth = request.getParameter("month");
	    String paramYear = request.getParameter("year");
	
	    if (paramMonth != null && paramYear != null) {
	        currentMonth = Integer.parseInt(paramMonth) - 1; // Convert to 0-based
	        currentYear = Integer.parseInt(paramYear);
	    }
	
	    calendar.set(currentYear, currentMonth, 1);
	    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	
	    String monthName = new DateFormatSymbols().getMonths()[currentMonth];
	    
		// Determine previous and next month/year
	    	int prevMonth = currentMonth - 1;
	        int prevYear = currentYear;
	        int nextMonth = currentMonth + 1;
	        int nextYear = currentYear;
	
	        if (prevMonth < 0) {
	            prevMonth = 11;
	            prevYear--;
	        }
	
	        if (nextMonth > 11) {
	            nextMonth = 0;
	            nextYear++;
	        }
	%>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <h3>Controls</h3>
        <!-- control the month and year of the selected one -->
        <div class="month-year-select">
        	<a href="?month=<%= prevMonth + 1 %>&year=<%= prevYear %>" class="next-prev">&lt;</a>
			<div class="current-month-year">
		        <span><%= monthName %> <%= currentYear %></span>
		    </div>
        	<a href="?month=<%= nextMonth + 1 %>&year=<%= nextYear %>" class="next-prev">&gt;</a>
        </div>
        
        <!-- Drop down to choose the Service -->
        <div>
	    	<select id="dropdown" name="service" required>
	        	<option value="null" disabled selected>Select a service</option>
	          	<option value="cleaning">Cleaning</option>
	          	<option value="laundry">Laundry</option>
		        <option value="carpet-cleaning">Carpet Cleaning</option>
		        <option value="window-cleaning">Window Cleaning</option>
	        </select>
	        <br><br>
	    </div>
	    <hr style="width: 80%">
	    
	    <!-- Your Bookings -->
	    <div>
	    	<%
	    		try {
	    			
	    		}
	    		catch(Exception e) {
	    			out.println("Error :" + e);
	    		}
	    	%>
	    </div>	    
    </div>
<!--  -------------------------------------- End of Side Bar -------------------------------------------- -->
    <!-- Calendar Container -->
    <div class="calendar-container">
        <h1>Monthly Booking Calendar</h1>
        <div class="month-year" style="margin-left: auto; margin-right:auto;">
            <h2><%= monthName %> <%= currentYear %></h2>
        </div>
        <div class="calendar">
            <!-- Day Headers -->
            <div class="header">Sunday</div>
            <div class="header">Monday</div>
            <div class="header">Tuesday</div>
            <div class="header">Wednesday</div>
            <div class="header">Thursday</div>
            <div class="header">Friday</div>
            <div class="header">Saturday</div>

            <!-- Days of the Month -->
		    <% 
		        // Add empty slots for days before the first day of the month
		        for (int i = 1; i < firstDayOfWeek; i++) {
		    %>
		    <div class="day empty"></div>
		    <% } %>
		
		    <% 
		        // Render days of the month
		        for (int day = 1; day <= daysInMonth; day++) {
		            String dayClass = isCurrentMonth ? "day" : "day disabled";
		    %>
		    <div class="<%= dayClass %>" <%= isCurrentMonth ? "onclick='bookSlot(" + day + ", " + (currentMonth + 1) + ", " + currentYear + ")'" : "" %>>
		        <%= day %>
		    </div>
		    <% } %>
		</div>
    </div>

	<!-- Hidden form to submit date data to the servlet -->
	<form id="bookingForm" action="YourServletURL" method="POST" style="display: none;">
	    <input type="hidden" name="booking_date" id="selectedDate">
	</form>

    <script>
        function bookSlot(day, month, year) {
            alert(`You selected ${day}/${month}/${year} for booking.`);
            // Add additional booking logic here
            const formattedDate = year + '-' + month + '-' + day;
            
            document.getElementById("selectedDate").value = formattedDate;
            
        }
    </script>

</body>
</html>