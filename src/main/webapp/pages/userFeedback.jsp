<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import ="java.sql.*"%>
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
		
      	#container { 
        	width: 55%; 
	        margin: auto; 
	        border: #a3a3a3 3px solid; 
	        padding: 10px; 
	        border-radius: 15px;
	        background-color: #fff6d3;
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
      <form action="../backend/userFeedbackLogic.jsp" method="post">
      
	<%
      	// === Declarations ===
     	int ratingCount = 0;
        int commentCount = 0;
	  	String dbUrl = "jdbc:mysql://localhost:3306/db1";
	  	String dbUser = "root";
	  	String dbPassword = "root";
	  	String dbClass = "com.mysql.jdbc.Driver";
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
	  		String sqlStr = "SELECT * FROM member";
	      	rs = stmt.executeQuery(sqlStr);      	
	      	
	      	// --- loop questions and create dynamic form ---
	      	while (rs.next()) {
	      		String dbQuestionType = rs.getString("question_type");
	      		String dbQuestionText = rs.getString("question_text");
	      		
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
						<input type="radio" name="<%= dbQuestionType + ratingCount %>" value="<%= i + 1 %>" required>
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
			        <input type="text" name="<%= dbQuestionType + commentCount %>" placeholder="Type here..." required>
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
	</div>
</body>
</html>