<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import ="java.sql.*"%>
<%@ page import ="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Feedback Session</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
	<link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/feedbackPage.css">
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

</head>
<body>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp" %>
	</div>
	
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
			// Retrieve the list from the request
			@SuppressWarnings("unchecked")
	        List<Map<String, String>> questions = (List<Map<String, String>>) session.getAttribute("questions");
		  	
		  	try {
			    if (questions != null) {
			    	// --- loop questions and create dynamic form ---
			        for (Map<String, String> question : questions) {
			            String questionType = (String) question.get("question_type");
			            String questionText = (String) question.get("question_text");
			            String strQuestionId = (String) question.get("question_id");
			            int questionId = Integer.parseInt(strQuestionId);
			
			            if ("Rating".equals(questionType)) { // Replace "Rating" with your actual type
		%>
						<label for="<%= questionId %>"><%= questionText %></label><br>
					    <span class="most-least">Very unsatisfied</span>
						<%		
							for(int i = 0; i < 5; i++) { // generate 5 radio buttons
						%>
	
								<input type="radio" name="<%= questionId %>" value="<%= i + 1 %>" required> <!-- name = Rating1 -->
						<%
							}
						%>
						<span class="most-least">Very satisfied</span>
	        			<br><br><br>
	    			<%
						}
						else { // else this is a open ended question						
							// Start building question forms for the questions
					%>
							<label for="<%= questionId %>"><%= questionText %></label><br>
					        <input type="text" name="<%= questionId %>" placeholder="Type here..." required>
					        <br><br>
		<%
						}
			 		}
		  		}
		  	}
		  	catch(Exception e) {
		  		out.println("Error :" + e);
		  	}
	   
		%>
	
	        <!-- Submit btn -->
	        <button type="submit" id="submit-feedback-btn" >Submit</button>
	      </form>
	    </article>
    	<!-- Thank you overlay -->
	    <div id="thankYouOverlay" class="overlay">
	    	<div class="popup">
	        	<h2>Thank You for Your Feedback!</h2>
	            <button onclick="window.location.href='<%=request.getContextPath()%>/pages/homePage.jsp'">Continue</button>
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