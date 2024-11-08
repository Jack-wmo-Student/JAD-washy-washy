<%--
	
 --%>

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
      Lorem, ipsum dolor sit amet consectetur adipisicing elit. Labore quod a ut!
      Officiis libero modi soluta tempora dolorem unde obcaecati eum dicta odit 
      quos at, quo enim, earum culpa provident!
    </section>
    <hr><br>

    <!-- Questions -->
    <article>
      <form action="/" method="post"> <!-- Need to make this dynamic -->
        <!-- Service Dropdown -->
        <label for="dropdown">Which Service did you choose?</label><br>
        <select id="dropdown" name="service">
          <option value="cleaning">Cleaning</option>
          <option value="laundry">Laundry</option>
          <option value="carpet-cleaning">Carpet Cleaning</option>
          <option value="window-cleaning">Window Cleaning</option>
        </select>
        <br><br>

        <!-- Ratings -->
        <!-- rating 1 -->
        <label for="rating1">How Satisfied are you with the overall quality of our cleaning service? </label><br>
        <span class="most-least">Very satisfied</span>
        <input type="radio" name="rating1" value="1">
        <input type="radio" name="rating1" value="3">
        <input type="radio" name="rating1" value="3">
        <input type="radio" name="rating1" value="4">
        <input type="radio" name="rating1" value="5">
        <span class="most-least">Very unsatisfied</span>
        <br><br><br>

        <!-- rating 1 -->
        <label for="rating1">How Satisfied are you with the overall quality of our cleaning service?</label><br>
        <span class="most-least">Very satisfied</span>
        <input type="radio" name="rating2" value="1">
        <input type="radio" name="rating2" value="3">
        <input type="radio" name="rating2" value="3">
        <input type="radio" name="rating2" value="4">
        <input type="radio" name="rating2" value="5">
        <span class="most-least">Very unsatisfied</span>
        <br><br><br>
        
        <!-- rating 2 -->
        <label for="rating1">How professional and courteous were our cleaning staffs?</label><br>
        <span class="most-least">Very satisfied</span>
        <input type="radio" name="rating3" value="1">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="4">
        <input type="radio" name="rating3" value="5">
        <span class="most-least">Very unsatisfied</span>
        <br><br><br>

        <!-- rating 3 -->
        <label for="rating1">How would you rate the cleanliness of the areas we serviced?</label><br>
        <span class="most-least">Very satisfied</span>
        <input type="radio" name="rating3" value="1">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="4">
        <input type="radio" name="rating3" value="5">
        <span class="most-least">Very unsatisfied</span>
        <br><br><br>
        
        <!-- rating 4 -->
        <label for="rating1">How easy was it to book our cleaning appointment with us?</label><br>
        <span class="most-least">Very satisfied</span>
        <input type="radio" name="rating3" value="1">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="3">
        <input type="radio" name="rating3" value="4">
        <input type="radio" name="rating3" value="5">
        <span class="most-least">Very unsatisfied</span>
        <br><br><br><br>


        <!-- Open ended questions -->
        <!-- Question 1 -->
        <label for="name">Are there any additional cleaning services you would like us to offer?</label><br>
        <input type="text" name="Q1" placeholder="Type here...">
        <br><br>

        <!-- Question 2 -->
        <label for="name">Do you have any suggestions for how we could improve our solutions?</label><br>
        <input type="text" name="Q1" placeholder="Type here...">
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
</body>
</html>