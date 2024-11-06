// ProfitMarginsDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ProfitMarginsDAO {
    public List<ProfitMargin> getProfitMargins() {
        List<ProfitMargin> margins = new ArrayList<>();
        String query = "SELECT month, profit FROM profit_margins";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                margins.add(new ProfitMargin(rs.getString("month"), rs.getBigDecimal("profit")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return margins;
    }
}

public class BooksPerMonthDAO {
    public List<BooksPerMonth> getBooksPerMonth() {
        List<BooksPerMonth> books = new ArrayList<>();
        String query = "SELECT month, book_count FROM books_per_month";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(new BooksPerMonth(rs.getString("month"), rs.getInt("book_count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}

public class FeedbackRatingsDAO {
    public List<FeedbackRating> getFeedbackRatings() {
        List<FeedbackRating> feedbacks = new ArrayList<>();
        String query = "SELECT rating, feedback FROM feedback_ratings";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                feedbacks.add(new FeedbackRating(rs.getInt("rating"), rs.getString("feedback")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedbacks;
    }
}

//ProfitMargin.java

public class ProfitMargin {
 private String month;
 private BigDecimal profit;

 public ProfitMargin(String month, BigDecimal profit) {
     this.month = month;
     this.profit = profit;
 }

 // Getters and Setters
}

//BooksPerMonth.java
public class BooksPerMonth {
 private String month;
 private int bookCount;

 public BooksPerMonth(String month, int bookCount) {
     this.month = month;
     this.bookCount = bookCount;
 }

 // Getters and Setters
}

//FeedbackRating.java
public class FeedbackRating {
 private int rating;,
 
 private String feedback;

 public FeedbackRating(int rating, String feedback) {
     this.rating = rating;
     this.feedback = feedback;
 }

 // Getters and Setters
}
