<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
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
      <form onsubmit="feedbackSubmit(event)" method="post">
        <!-- Service Dropdown -->
        <label for="dropdown">Which Service did you choose?</label><br>
        <select id="dropdown" name="service" required>
          <option value="null" disabled selected>Select a service</option>
          <option value="cleaning">Cleaning</option>
          <option value="laundry">Laundry</option>
          <option value="carpet-cleaning">Carpet Cleaning</option>
          <option value="window-cleaning">Window Cleaning</option>
        </select>
        <br><br>

        <!-- Ratings -->
        <!-- rating 1 -->
        <label for="rating1">How Satisfied are you with the overall quality of our cleaning service? </label><br>
        <span class="most-least">Very unsatisfied</span>
        <input type="radio" name="question1" value="1" required>
        <input type="radio" name="question1" value="2" required>
        <input type="radio" name="question1" value="3" required>
        <input type="radio" name="question1" value="4" required>
        <input type="radio" name="question1" value="5" required>
        <span class="most-least">Very satisfied</span>
        <br><br><br>

        <!-- rating 1 -->
        <label for="rating1">How Satisfied are you with the overall quality of our cleaning service?</label><br>
        <span class="most-least">Very unsatisfied</span>
        <input type="radio" name="rating2" value="1" required>
        <input type="radio" name="rating2" value="2" required>
        <input type="radio" name="rating2" value="3" required>
        <input type="radio" name="rating2" value="4" required>
        <input type="radio" name="rating2" value="5" required>
        <span class="most-least">Very satisfied</span>
        <br><br><br>
        
        <!-- rating 2 -->
        <label for="rating1">How professional and courteous were our cleaning staffs?</label><br>
        <span class="most-least">Very unsatisfied</span>
        <input type="radio" name="rating3" value="1" required>
        <input type="radio" name="rating3" value="2" required>
        <input type="radio" name="rating3" value="3" required>
        <input type="radio" name="rating3" value="4" required>
        <input type="radio" name="rating3" value="5" required>
        <span class="most-least">Very satisfied</span>
        <br><br><br>

        <!-- rating 3 -->
        <label for="rating1">How would you rate the cleanliness of the areas we serviced?</label><br>
        <span class="most-least">Very unsatisfied</span>
        <input type="radio" name="rating4" value="1" required>
        <input type="radio" name="rating4" value="2" required>
        <input type="radio" name="rating4" value="3" required>
        <input type="radio" name="rating4" value="4" required>
        <input type="radio" name="rating4" value="5" required>
        <span class="most-least">Very satisfied</span>
        <br><br><br>
        
        <!-- rating 4 -->
        <label for="rating1">How easy was it to book our cleaning appointment with us?</label><br>
        <span class="most-least">Very unsatisfied</span>
        <input type="radio" name="rating5" value="1" required>
        <input type="radio" name="rating5" value="2" required>
        <input type="radio" name="rating5" value="3" required>
        <input type="radio" name="rating5" value="4" required>
        <input type="radio" name="rating5" value="5" required>
        <span class="most-least">Very satisfied</span>
        <br><br><br><br>


        <!-- Open ended questions -->
        <!-- Question 1 -->
        <label for="name">Are there any additional cleaning services you would like us to offer?</label><br>
        <input type="text" name="Q1" placeholder="Type here..." required>
        <br><br>

        <!-- Question 2 -->
        <label for="name">Do you have any suggestions for how we could improve our solutions?</label><br>
        <input type="text" name="Q1" placeholder="Type here..." required>
        <br><br>

        <!-- Question 3 -->
        <label for="name">Ant Remarks?</label><br>
        <input type="text" name="Q1" placeholder="Type here...">
        <br><br>


        <!-- Submit btn -->
        <button type="submit" id="submit-feedback-btn" >Submit</button>
      </form>
    </article>
	</div>
	<script>
	    function feedbackSubmit(event) {
	      event.preventDefault();
	
	      const dropdown = document.getElementById('dropdown');
	      const selectedValue = dropdown.value;
	      console.log(selectedValue);
	
	      if (selectedValue === "null") {
	        dropdown.classList.add('error');
	        dropdown.scrollIntoView({ behavior: 'smooth', block: 'center' });
	      } else {
	        dropdown.classList.remove('error');	
	        window.location.href = '../backend/userFeedbackLogic.jsp?';
	      }
	    }
	</script>
</body>
</html>