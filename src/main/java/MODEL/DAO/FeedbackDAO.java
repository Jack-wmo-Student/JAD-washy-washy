package MODEL.DAO;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DBACCESS.DBConnection;

public class FeedbackDAO {
	public static List<Map<String, String>> getQuestions() throws SQLException {
		System.out.println("We are now in getQuestions");
		// set the variables
		String query = "SELECT * FROM question";
		List<Map<String, String>> questionList = new ArrayList<>();
		
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
		    	Map<String, String> question = new HashMap<>();
		        question.put("question_id", rs.getString("question_id"));
		        System.out.printf("These are the questions", rs.getString("question_id"));
		        question.put("question_text", rs.getString("question_text"));
		        question.put("question_type", rs.getString("question_type"));
		        questionList.add(question);
		    }
			
			return questionList;
			
		} catch (Exception e) {
			System.out.println("----- Error in getQuestion -----");
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public static Integer createFeedbackWithDefaultValues() throws SQLException {
		System.out.println("We are now in createFeedbackWithDefaultValues");
		
		// set the variables
		int feedback_id = -1;
		String query = "INSERT INTO feedback DEFAULT VALUES RETURNING feedback_id";
		
		
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			
			while (rs.next()) {
				feedback_id = rs.getInt("feedback_id");
			}
			
		} catch (Exception e) {
			System.out.println("----- Error in createFeedbackWithDefaultValues -----");
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}
		
		return feedback_id;
	}
	
	public static void updateBookingWithFeedbackId(List<Integer> booking_id_lists, Integer feedback_id) throws SQLException{
		System.out.println("We are now in createFeedbackResponse");
		
		// set the variable
		String query = "UPDATE booking SET feedback_id = ? WHERE booking_id = ?";
		
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			
			for(Integer bookingId : booking_id_lists) {
                pstmt.setInt(1, feedback_id);
                pstmt.setInt(2, bookingId);
                pstmt.executeUpdate();
            }	
		} catch (Exception e) {
			System.out.println("----- Error in createFeedbackResponse -----");
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public static void createFeedbackResponse(Integer feedback_id, Integer question_id, String param_value) throws SQLException{
		System.out.println("We are now in createFeedbackResponse");
		
		// set the variable
		String query = "INSERT INTO response(feedback_id, question_id, response_value) VALUES (?, ?, ?)";
		
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, feedback_id);
            pstmt.setInt(2, question_id);
            pstmt.setString(3, param_value);
            
            pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("----- Error in createFeedbackResponse -----");
			System.out.println("Database connection or query execution failed.");
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
}












