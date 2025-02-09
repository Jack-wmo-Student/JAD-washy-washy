<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import ="java.sql.*, java.util.*"%>
<%@ page import ="CONTROLLER.FeedbackController" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Feedback Session</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/navbar.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/feedbackPage.css">
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="icon" href="<%=request.getContextPath()%>/assets/icons/favicon.ico" type="image/x-icon">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
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

    <div class="page-wrapper">
        <main class="feedback-container">
            <header class="feedback-header">
            	<!-- Title -->
                <h1>How Was Your Experience?</h1>
                
                <!-- Description -->
                <p>Your feedback helps us improve our service and provide a better experience for everyone.
                   We appreciate you taking the time to share your thoughts.</p>
            </header>

			<!-- Questions -->
            <form action="<%=request.getContextPath()%>/FeedbackController" method="POST" class="feedback-form">
                <%          
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> questions = (List<Map<String, String>>) session.getAttribute("questions");
                    
                    try {
                        if (questions != null) {
                            for (Map<String, String> question : questions) {
                                String questionType = (String) question.get("question_type");
                                String questionText = (String) question.get("question_text");
                                String strQuestionId = (String) question.get("question_id");
                                int questionId = Integer.parseInt(strQuestionId);

                                if ("Rating".equals(questionType)) {
                %>
                                <div class="form-group">
                                    <label for="<%= questionId %>"><%= questionText %></label>
                                    <div class="rating-scale">
                                        <span class="scale-label">Very unsatisfied</span>
                                        <div class="radio-group">
                                            <%
                                                for(int i = 0; i < 5; i++) {
                                            %>
                                                <div class="radio-option">
                                                    <input type="radio" name="<%= questionId %>" value="<%= i + 1 %>" required>
                                                </div>
                                            <%
                                                }
                                            %>
                                        </div>
                                        <span class="scale-label">Very satisfied</span>
                                    </div>
                                </div>
                            <%
                                } else {
                            %>
                                <div class="form-group">
                                    <label for="<%= questionId %>"><%= questionText %></label>
                                    <input type="text" name="<%= questionId %>" placeholder="Share your thoughts with us..." required>
                                </div>
                            <%
                                }
                            }
                        }
                    } catch(Exception e) {
                        out.println("Error: " + e);
                    }
                %>

				<!-- Submit button -->
                <button type="submit" class="submit-button">Submit Feedback</button>
            </form>
        </main>
    </div>

	<!-- Thank you overlay -->
    <div id="thankYouOverlay" class="overlay">
        <div class="success-popup popup">
            <h2>Thank You!</h2>
            <p>Your feedback has been submitted successfully. We truly value your input.</p>
            <button onclick="closeOverlay(); window.location.href='<%=request.getContextPath()%>/pages/homePage.jsp'">
                Return to Homepage
            </button>
        </div>
    </div>

    <%
        Boolean feedbackSuccess = (Boolean) request.getAttribute("feedbackSuccess");
        if (feedbackSuccess != null && feedbackSuccess) {
    %>
        <script>
            showOverlay();
        </script>
    <% } %>
</body>
</html>