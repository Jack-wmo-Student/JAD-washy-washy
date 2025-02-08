package MODEL.DAO;

import MODEL.CLASS.TransactionHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import DBACCESS.DBConnection;

// TransactionHistoryDAO.java
public class TransactionHistoryDAO {
    public List<TransactionHistory> getUserTransactions(int userId) throws SQLException {
        List<TransactionHistory> transactions = new ArrayList<>();
        
        String query = """
            SELECT 
                b.booking_id,
                b.booked_date,
                s.service_name,
                p.product_price as price,
                p.payment_method,
                ps.status_name as payment_status,
                bs.status_name as booking_status,
                p.created_at
            FROM booking b
            JOIN service s ON b.service_id = s.service_id
            JOIN payment p ON b.booking_id = p.booking_id
            JOIN status ps ON p.payment_status = ps.status_id
            JOIN status bs ON b.status_id = bs.status_id
            WHERE b.booked_by_user_id = ?
            ORDER BY p.created_at DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TransactionHistory transaction = new TransactionHistory();
                transaction.setBookingId(rs.getInt("booking_id"));
                transaction.setBookedDate(rs.getDate("booked_date"));
                transaction.setServiceName(rs.getString("service_name"));
                transaction.setPrice(rs.getDouble("price"));
                transaction.setPaymentMethod(rs.getString("payment_method"));
                transaction.setPaymentStatus(rs.getString("payment_status"));
                transaction.setBookingStatus(rs.getString("booking_status"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));
                
                transactions.add(transaction);
            }
        }
        return transactions;
    }
}