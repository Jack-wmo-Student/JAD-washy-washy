<%-- <%@ page import="java.util.List" %>
<%@ page import="ProfitMarginsDAO, BooksPerMonthDAO, FeedbackRatingsDAO" %>
<%@ page import="ProfitMargin, BooksPerMonth, FeedbackRating" %>

<%
    ProfitMarginsDAO profitMarginsDAO = new ProfitMarginsDAO();
    BooksPerMonthDAO booksPerMonthDAO = new BooksPerMonthDAO();
    FeedbackRatingsDAO feedbackRatingsDAO = new FeedbackRatingsDAO();

    List<ProfitMargin> profitMargins = profitMarginsDAO.getProfitMargins();
    List<BooksPerMonth> booksPerMonth = booksPerMonthDAO.getBooksPerMonth();
    List<FeedbackRating> feedbackRatings = feedbackRatingsDAO.getFeedbackRatings();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Stats Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { width: 80%; margin: auto; }
        .section { margin: 20px 0; }
        h2 { color: #333; }
    </style>
</head>
<body>
<div class="container">
    <h1>Admin Dashboard</h1>

    <div class="section">
        <h2>Profit Margins</h2>
        <table border="1">
            <tr><th>Month</th><th>Profit</th></tr>
            <%
                for (ProfitMargin pm : profitMargins) {
                    out.println("<tr><td>" + pm.getMonth() + "</td><td>" + pm.getProfit() + "</td></tr>");
                }
            %>
        </table>
    </div>

    <div class="section">
        <h2>Books Per Month</h2>
        <table border="1">
            <tr><th>Month</th><th>Book Count</th></tr>
            <%
                for (BooksPerMonth bm : booksPerMonth) {
                    out.println("<tr><td>" + bm.getMonth() + "</td><td>" + bm.getBookCount() + "</td></tr>");
                }
            %>
        </table>
    </div>

    <div class="section">
        <h2>Feedback and Ratings</h2>
        <table border="1">
            <tr><th>Rating</th><th>Feedback</th></tr>
            <%
                for (FeedbackRating fr : feedbackRatings) {
                    out.println("<tr><td>" + fr.getRating() + "</td><td>" + fr.getFeedback() + "</td></tr>");
                }
            %>
        </table>
    </div>
</div>
</body>
</html> --%>