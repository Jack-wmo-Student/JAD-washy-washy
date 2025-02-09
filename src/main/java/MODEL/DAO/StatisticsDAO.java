package MODEL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DBACCESS.DBConnection;
import MODEL.CLASS.ServiceStatistics;

public class StatisticsDAO {
    
    public List<ServiceStatistics> getMostProfitableServices(String timeframe) throws SQLException {
        String sql = """
            SELECT 
                s.service_id,
                s.service_name,
                COUNT(b.booking_id) as total_bookings,
                SUM(p.product_price) as total_revenue,
                (SUM(p.product_price) / COUNT(b.booking_id)) as profit_per_booking
            FROM service s
            LEFT JOIN booking b ON s.service_id = b.service_id
            LEFT JOIN payment p ON b.booking_id = p.booking_id
            WHERE b.booked_date >= ? AND b.booked_date <= ?
            GROUP BY s.service_id, s.service_name
            ORDER BY total_revenue DESC
        """;

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (timeframe.toLowerCase()) {
            case "this month":
                startDate = endDate.withDayOfMonth(1);
                break;
            case "last month":
                startDate = endDate.minusMonths(1).withDayOfMonth(1);
                endDate = endDate.withDayOfMonth(1).minusDays(1);
                break;
            case "last 3 months":
                startDate = endDate.minusMonths(3);
                break;
            default:
                startDate = endDate.minusMonths(1);
        }

        List<ServiceStatistics> statistics = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statistics.add(new ServiceStatistics(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getDouble("total_revenue"),
                        rs.getInt("total_bookings"),
                        rs.getDouble("profit_per_booking"),
                        timeframe
                    ));
                }
            }
        }
        
        return statistics;
    }

    public List<ServiceStatistics> getLeastProfitableServices(String timeframe) throws SQLException {
        // Similar to getMostProfitableServices but with ORDER BY total_revenue ASC
        String sql = """
            SELECT 
                s.service_id,
                s.service_name,
                COUNT(b.booking_id) as total_bookings,
                SUM(p.product_price) as total_revenue,
                (SUM(p.product_price) / COUNT(b.booking_id)) as profit_per_booking
            FROM service s
            LEFT JOIN booking b ON s.service_id = b.service_id
            LEFT JOIN payment p ON b.booking_id = p.booking_id
            WHERE b.booked_date >= ? AND b.booked_date <= ?
            GROUP BY s.service_id, s.service_name
            ORDER BY total_revenue ASC
        """;

        // Rest of the implementation is similar to getMostProfitableServices
        // Implementation follows the same pattern as getMostProfitableServices
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (timeframe.toLowerCase()) {
            case "this month":
                startDate = endDate.withDayOfMonth(1);
                break;
            case "last month":
                startDate = endDate.minusMonths(1).withDayOfMonth(1);
                endDate = endDate.withDayOfMonth(1).minusDays(1);
                break;
            case "last 3 months":
                startDate = endDate.minusMonths(3);
                break;
            default:
                startDate = endDate.minusMonths(1);
        }

        List<ServiceStatistics> statistics = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statistics.add(new ServiceStatistics(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getDouble("total_revenue"),
                        rs.getInt("total_bookings"),
                        rs.getDouble("profit_per_booking"),
                        timeframe
                    ));
                }
            }
        }
        
        return statistics;
    }

    public List<ServiceStatistics> getBookingsPerService(String timeframe) throws SQLException {
        String sql = """
            SELECT 
                s.service_id,
                s.service_name,
                COUNT(b.booking_id) as total_bookings,
                SUM(p.product_price) as total_revenue,
                AVG(p.product_price) as avg_revenue_per_booking
            FROM service s
            LEFT JOIN booking b ON s.service_id = b.service_id
            LEFT JOIN payment p ON b.booking_id = p.booking_id
            WHERE b.booked_date >= ? AND b.booked_date <= ?
            GROUP BY s.service_id, s.service_name
            ORDER BY total_bookings DESC
        """;

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (timeframe.toLowerCase()) {
            case "this month":
                startDate = endDate.withDayOfMonth(1);
                break;
            case "last month":
                startDate = endDate.minusMonths(1).withDayOfMonth(1);
                endDate = endDate.withDayOfMonth(1).minusDays(1);
                break;
            case "last 3 months":
                startDate = endDate.minusMonths(3);
                break;
            default:
                startDate = endDate.minusMonths(1);
        }

        List<ServiceStatistics> statistics = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statistics.add(new ServiceStatistics(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getDouble("total_revenue"),
                        rs.getInt("total_bookings"),
                        rs.getDouble("avg_revenue_per_booking"),
                        timeframe
                    ));
                }
            }
        }
        
        return statistics;
    }
}