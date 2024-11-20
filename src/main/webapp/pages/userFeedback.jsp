<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import ="java.sql.*"%>
<%@ page import ="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Feedback Session</title>
	<style>
      	body { font-family: Arial, sans-serif; padding: 10px 0; background-color: #B7E0FF;}
      	section { margin: 20px 0; }
     	h2 { color: #333; }
      	label {font-size: large; padding-left: 10px;}
		.most-least {padding: 0px 15px;}
		#feedback-title {padding-left: 0.5em; border-left: 8px solid #e46b59;}
		hr {border-color: #FFCFB3;}

		.error {
		    border-color: red !important; 
		}
		
		/* Overlay Styles */
        .overlay {
            display: none; /* Hidden by default */
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.7);
            color: white;
            z-index: 1000;
            text-align: center;
            padding-top: 200px;
        }
        .overlay .popup {
            background: #444;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            display: inline-block;
        }
        .overlay .popup button {
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 20px;
        }
		
      	#container { 
        	width: 55%; 
	        margin: auto;
	        padding: 10px; 
	        border-radius: 15px;
	        background-color: #fff6d3;
	        box-shadow: 10px 10px 5px lightblue;
      	}

	   	#dropdown {
	        padding: 8px 10px;
	        margin: 15px 13px;
	        width: 90%;
	        border-radius: 5px;
	        border-color: #a3a3a3;
	    }
	
	    input[type="text"] {
	        padding: 10px; 
	        border-radius: 5px; 
	        border-color: #a3a3a3;
	        width: 90%; 
	        margin: 15px 13px; 
	    }
	
	    input[type="radio"] {
	        transform: scale(2); /* Increase the scale as needed */
	        margin: 25px 40px 13px 45px;
	    }
	
	    #submit-feedback-btn {
	        padding: 10px 20px;
	        border-radius: 5px; 
	        background-color: rgb(95, 255, 98); 
	        margin-left: 85%;
	        margin-bottom: 10px;
	    }    
    </style>
	<script>
        // Function to show the overlay
        function showOverlay() {
            var overlay = document.getElementById('thankYouOverlay');
            overlay.style.display = 'block';
        }

        // Function to close the overlay
        function closeOverlay() {
            var overlay = document.getElementById('thankYouOverlay');
            overlay.style.display = 'none';
        }
    </script>

	<%	
		// Check inside the cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            // Check for the specific cookie by name
	            if ("isLoggedIn".equals(cookie.getName())) {
	                out.println("Hello, " + cookie.getValue() + "!");
	            }
	        }
	    } else {
	        out.println("No cookies found!");
	    }
	
	
		String dbClass = System.getenv("DB_CLASS");
	    String dbUrl = System.getenv("DB_URL");
	    String dbPassword = System.getenv("DB_PASSWORD");
	    String dbUser = System.getenv("DB_USER");
	%>
</head>
<body>
	<div id="container">
	    <!-- Title -->
			<header>
	      <h1 id="feedback-title">Feedback Form</h1>
	    </header>
	
	    <!-- Description -->
	     <hr>
	    <section>
	      <p>
	        Thank you for choosing Washy Washy! We're committed to providing
	        top-notch cleaning services, and your feedback helps us improve.
	        Please take a moment to let us know about your experience so we 
	        can better serve you in the future.
	      </p>
	      
	    </section>
	    <hr><br>
	
	    <!-- Questions -->
	    <article>
	      <form action="feedbackLogic" method="post">
	      
		<%
	      	// === Declarations ===
	     	int ratingCount = 0;
	        int commentCount = 0;
		  	String[] questionTypes = {"Rating", "Comments"};
		  	Connection conn = null;
		  	Statement stmt = null;
		  	ResultSet rs = null;
		  	
		  	try {
		  		// === Connect to Database ===
		  		Class.forName(dbClass);
		  		conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		  		stmt = conn.createStatement();
		  		
		  		// --- fetch Data ---
		  		String sqlStr = "SELECT * FROM question";
		      	rs = stmt.executeQuery(sqlStr);
		      	
		      	// --- loop questions and create dynamic form ---
		      	while (rs.next()) {
		      		String dbQuestionType = rs.getString("question_type");
		      		String dbQuestionText = rs.getString("question_text");
		      		int dbQuestionId = rs.getInt("question_id");
		      		
					if(questionTypes[0].equals(dbQuestionType)) {
						// if the question type is Rating, do this...
						ratingCount++;
						
						// Start building the rating
		%>
						<label for="<%= dbQuestionType + ratingCount %>"><%= dbQuestionText %></label><br>
					    <span class="most-least">Very unsatisfied</span>
					<%		
						for(int i = 0; i < 5; i++) { // generate 5 radio buttons
					%>
	
							<input type="radio" name="<%= dbQuestionId %>" value="<%= i + 1 %>" required> <!-- name = Rating1 -->
					<%
						}
					%>
						<span class="most-least">Very satisfied</span>
	        			<br><br><br>
	    		<%
					}
					else { // else this is a open ended question
						commentCount++;
						
						// Start building question forms for the questions
				%>
						<label for="<%= dbQuestionType + commentCount %>"><%= dbQuestionText %></label><br>
				        <input type="text" name="<%= dbQuestionId %>" placeholder="Type here..." required>
				        <br><br>
		<%
					}
			 	}
		  	}
		  	catch(Exception e) {
		  		out.println("Error :" + e);
		  	}
		  	finally {
		  	    // Close resources in reverse order of their creation
		  	    try {
		  	    	if (rs != null) rs.close();
		  	        if (stmt != null) stmt.close();
		  	        if (conn != null) conn.close();
		  	    } catch (Exception e) {
		  	        out.println("Error closing resources: " + e);
		  	    }
		  	}
	   
		%>

	        <!-- Service Dropdown -->
	        <!-- <label for="dropdown">Which Service did you choose?</label><br>
	        <select id="dropdown" name="service" required>
	          <option value="null" disabled selected>Select a service</option>
	          <option value="cleaning">Cleaning</option>
	          <option value="laundry">Laundry</option>
	          <option value="carpet-cleaning">Carpet Cleaning</option>
	          <option value="window-cleaning">Window Cleaning</option>
	        </select>
	        <br><br> -->
	
	
	        <!-- Submit btn -->
	        <button type="submit" id="submit-feedback-btn" >Submit</button>
	      </form>
	    </article>
    <!-- Thank you overlay -->
	    <div id="thankYouOverlay" class="overlay">
	    	<div class="popup">
	        	<h2>Thank You for Your Feedback!</h2>
	            <button onclick="window.location.href = 'nextPage.jsp';">Continue</button>
	        </div>
	    </div>

	    <!-- Show the overlay if the feedbackSuccess attribute is set -->
        <%
            Boolean feedbackSuccess = (Boolean) request.getAttribute("feedbackSuccess");
            if (feedbackSuccess != null && feedbackSuccess) {
        %>
            <script>
                showOverlay(); // Show the thank-you overlay if submission was successful
            </script>
        <% } %>
	</div>
</body>
</html>