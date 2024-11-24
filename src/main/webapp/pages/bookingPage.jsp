<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar, java.text.DateFormatSymbols, java.util.List, jakarta.servlet.http.HttpSession, java.util.Map, java.util.ArrayList, java.util.HashMap" %>
<%@ page import="model.booking, model.timeslot, model.category, model.service" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Monthly Booking Calendar</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
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
            height: 100%;
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
        .bookings-container {
		    width: 80%;
		    margin: 0 auto;
		    font-family: Arial, sans-serif;
		}
		
		.booking-item {
		    background-color: #f9f9f9;
		    border: 1px solid #ddd;
		    border-radius: 8px;
		    padding: 15px;
		    margin-bottom: 10px;
		    cursor: pointer;
		    transition: background-color 0.3s ease;
		}
		
		.booking-item:hover {
		    background-color: #f0f0f0;
		}
		
		.booking-item h3 {
		    margin: 0;
		    font-size: 1.2em;
		    color: #333;
		}
		
		.booking-item p {
		    margin: 5px 0;
		    color: #666;
		}
		
		/* Modal overlay styles */
		#bookingModal {
		    display: none;
		    position: fixed;
		    top: 0;
		    left: 0;
		    width: 100%;
		    height: 100%;
		    background-color: rgba(0, 0, 0, 0.5);
		    z-index: 1;
		}
		
		/* Modal content box */
		.modal-content {
		    background-color: white;
		    padding: 20px;
		    border-radius: 8px;
		    width: 400px;
		    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
		    margin: 275px auto;
		}
		
		.modal-content h2 {
		    margin-top: 0;
		    font-size: 1.5em;
		    color: #333;
		}
		
		.modal-content p {
		    font-size: 1.1em;
		    color: #666;
		}
		
		.modal-content button {
		    background-color: #007bff;
		    color: white;
		    border: none;
		    padding: 10px 15px;
		    border-radius: 5px;
		    cursor: pointer;
		    font-size: 1em;
		    margin-top: 15px;
		    display: block;
		    width: 100%;
		}
	
		.close-btn {
		    color: #aaa;
		    float: right;
		    font-size: 28px;
		    font-weight: bold;
		}
		
		.close-btn:hover,
		.close-btn:focus {
		    color: black;
		    text-decoration: none;
		    cursor: pointer;
		}
		.day.disabled {
		    background-color: #e0e0e0; /* Gray background */
		    color: #999; /* Dimmed text color */
		    pointer-events: none; /* Disable clicking */
		}
		
		.day.empty {
		    background-color: #f0f0f0;
		}
    </style>
	<%
	    // Get current month and year dynamically or from request parameters for navigation
	    Calendar calendar = Calendar.getInstance();
	    int currentMonth = calendar.get(Calendar.MONTH); // 0-based
	    int currentYear = calendar.get(Calendar.YEAR);
	    boolean isCurrentMonth = (currentMonth == Calendar.getInstance().get(Calendar.MONTH)) && (currentYear == Calendar.getInstance().get(Calendar.YEAR));
	    String selectedService = request.getParameter("selectedService");
	
	    String paramMonth = request.getParameter("month");
	    String paramYear = request.getParameter("year");
	
	    // Get the Category service map
	    @SuppressWarnings("unchecked")
	    Map<category, List<service>> sessionCategoryServiceMap = 
	     	(Map<category, List<service>>) session.getAttribute("categoryServiceMap");
	    
	    List<Map<String, Object>> serviceLists = new ArrayList<>();
	    
	    // Get all the services from the map
	    for (Map.Entry<category, List<service>> entry : sessionCategoryServiceMap.entrySet()) {
	    	List<service> services = entry.getValue();
	    	
	    	for(service srv: services) {
	    		Map<String, Object> tempMap = new HashMap<>();
	    		tempMap.put("service_name", srv.getName().toString());
	    		tempMap.put("service_id", srv.getId());
	    		
	    		serviceLists.add(tempMap);
	    	}
	    }
	    
	    
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
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp" %>
	</div>
	
	<%
		String errorMessage = (String) request.getAttribute("errorMessage");

		if (errorMessage != null) {
	%>
			<script>
				alert(<%= errorMessage %>);
			</script>
	<%
		}
	%>

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
	        	<option value="null" disabled <% if(selectedService == null) {%> selected <%} %>>Select a service</option>
	        	<%
	        		for(Map<String, Object> service: serviceLists) {
	        	%>
	        		<option value="<%= service.get("service_id") %>" <% if (service.get("service_name").equals(selectedService)) { %> selected <% } %>>
	        			<%= service.get("service_name") %>
	        		</option>
	        	<%
	        		}
	        	%>
	        </select>
	        <br>
	    </div>
	    <hr style="width: 90%">
	    <br>
	    <!-- Your Bookings -->
	    <div>
	    	<%
	    		try {
	    			// Retrieve the session stuffs
	    			@SuppressWarnings("unchecked")
	    			List<booking> bookingLists = (List<booking>) session.getAttribute("bookingLists");
	    			
	    			if (bookingLists != null && !bookingLists.isEmpty()) {
	    				for(booking booking : bookingLists) {
	    					// Extract the booking details
	                        String serviceName = booking.getServiceName();
	                        String bookedDate = booking.getBookedDate().toString();
	                        String timeRange = booking.getTimeslot().getTimeRange();
	                        int bookingId = booking.getBookingId();
	        %>
	    					<!-- Display if have -->
	 						<div class="booking-item" 
		                         data-booking-id="<%= bookingId %>" 
		                         data-service-name="<%= serviceName %>" 
		                         data-booked-date="<%= bookedDate %>" 
		                         data-time-range="<%= timeRange %>"
		                         onclick="openModal(this)">
		                        <h3><%= serviceName %></h3>
		                        <p>Date: <%= bookedDate %></p>
		                        <p>Time: <%= timeRange %></p>
		                    </div>
		    <%
	    				}
	    			}
	    			else {
	    	%>
		    			<!-- No bookings message -->
	               		<p>You have no bookings at the moment.</p>
	    	<%
	    			}

	    		}
	    		catch(Exception e) {
	    			out.println("Error :" + e.getMessage());
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
		    <% } 
		        // Calculate and add empty slots after the last day to complete the week
		        int totalCells = firstDayOfWeek - 1 + daysInMonth; // Total cells filled so far
		        int remainingCells = 7 - (totalCells % 7); // Remaining cells to complete the last week
		
		        if (remainingCells < 7) { // Add empty cells only if they are needed
		            for (int i = 0; i < remainingCells; i++) {
		    %>
		                <div class="day empty"></div>
		    <% 
		            }
		        }
		    %>
		</div>
    </div>
    
    <!-- ----------------------- Model for showing booking details -------------------------- -->
	<div id="bookingModal" style="display:none;">
	    <div class="modal-content">
	    	<span class="close-btn" onclick="closeModal()">&times;</span>
	        <h2 id="modalServiceName"></h2>
	        <p id="modalBookedDate"></p>
	        <p id="modalTimeRange"></p>
	    </div>
	</div>
    

	<!-- Hidden form to submit date data to the servlet -->
	<form id="bookingForm" action="<%=request.getContextPath()%>/bookingPage" method="POST" style="display: none;">
	    <input type="hidden" name="booking_date" id="selectedDate">
	    <input type="hidden" name="service_booked_id" id="serviceId">
	</form>

    <script>
    	// ===== Funciton to redirect to booking slot =====
        function bookSlot(day, month, year) {
        	if(<%= isCurrentMonth %>) {
				const dropdown = document.getElementById("dropdown");
				
				if(dropdown.value == "null") {
		            dropdown.style.borderColor = "red";
				}
				else {
					// format the dates
		            const formattedDate = year + '-' + month + '-' + day;
					console.log('This is the formatted date that u have chosen: ', formattedDate);
					
		            document.getElementById("selectedDate").value = formattedDate;
		            document.getElementById("serviceId").value = document.getElementById('dropdown').value;
		            
		        	// Submit the form programmatically
		            document.getElementById("bookingForm").submit();
				}
			}
			else return;
        }
        
        // ===== Funtions for the booking details =====
       	function openModal(bookingElement) {
       	// Retrieve the data attributes from the clicked booking element
       	    var bookingId = bookingElement.getAttribute("data-booking-id");
       	    var serviceName = bookingElement.getAttribute("data-service-name");
       	    var bookedDate = bookingElement.getAttribute("data-booked-date");
       	    var timeRange = bookingElement.getAttribute("data-time-range");

       	    // Populate the modal with booking details
       	    document.getElementById("modalServiceName").innerText = "Service: " + serviceName;
       	    document.getElementById("modalBookedDate").innerText = "Date: " + bookedDate;
       	    document.getElementById("modalTimeRange").innerText = "Time: " + timeRange;
	
	        // Show the modal
	        document.getElementById("bookingModal").style.display = "block";
	    }
	
	    function closeModal() {
	        // Hide the modal
	        document.getElementById("bookingModal").style.display = "none";
	    }
	
	    // Placeholder function to simulate fetching booking details
	    function getBookingDetails(bookingId) {
	        // This is a mock of the data you would fetch
	        // You can replace this with an AJAX call or another method to get actual data
	        return {
	            serviceName: "Service Name " + bookingId,
	            bookedDate: "2024-11-25", // Example date
	            timeRange: "10am-11am",  // Example time slot
	            additionalInfo: "Some additional details about the booking."
	        };
	    }
        	
    </script>

</body>
</html>