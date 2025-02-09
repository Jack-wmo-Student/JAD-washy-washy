package MODEL.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import MODEL.CLASS.Service;
import DBACCESS.DBConnection;

public class ServiceDAO {
    private final String dbClass;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public ServiceDAO() {
        this.dbClass = System.getenv("DB_CLASS");
        this.dbUrl = System.getenv("DB_URL");
        this.dbUser = System.getenv("DB_USER");
        this.dbPassword = System.getenv("DB_PASSWORD");
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(dbClass);
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public int createService(String name, int categoryId, double price, int duration, String description, int statusId, String imageUrl) {
        String sql = "INSERT INTO public.service (service_name, category_id, price, duration_in_hour, service_description, status_id, image_url) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING service_id";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            stmt.setDouble(3, price);
            stmt.setInt(4, duration);
            stmt.setString(5, description);
            stmt.setInt(6, statusId);
            stmt.setString(7, imageUrl);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateService(int serviceId, String serviceName, double price, int duration, String description, String imageUrl)
            throws SQLException, ClassNotFoundException {
        String updateSQL = "UPDATE service SET service_name = ?, price = ?, duration_in_hour = ?, service_description = ?, image_url = ? WHERE service_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(updateSQL)) {
            ps.setString(1, serviceName);
            ps.setDouble(2, price);
            ps.setInt(3, duration);
            ps.setString(4, description);
            ps.setString(5, imageUrl);
            ps.setInt(6, serviceId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteService(int serviceId) throws SQLException, ClassNotFoundException {
        String deleteServiceTimeslotsSQL = "DELETE FROM service_timeslot WHERE service_id = ?";
        String deleteBookingsSQL = "DELETE FROM booking WHERE service_id = ?";
        String deleteServiceSQL = "DELETE FROM service WHERE service_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psServiceTimeslots = conn.prepareStatement(deleteServiceTimeslotsSQL)) {
                psServiceTimeslots.setInt(1, serviceId);
                psServiceTimeslots.executeUpdate();
            }

            try (PreparedStatement psBookings = conn.prepareStatement(deleteBookingsSQL)) {
                psBookings.setInt(1, serviceId);
                psBookings.executeUpdate();
            }

            try (PreparedStatement psService = conn.prepareStatement(deleteServiceSQL)) {
                psService.setInt(1, serviceId);
                int serviceRowsDeleted = psService.executeUpdate();
                conn.commit();
                return serviceRowsDeleted > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Service> getServicesByCategory(int categoryId) throws SQLException, ClassNotFoundException {
        List<Service> services = new ArrayList<>();
        String selectSQL = "SELECT * FROM service WHERE category_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    services.add(new Service(
                        rs.getInt("service_id"), 
                        rs.getInt("category_id"), 
                        rs.getInt("status_id"),
                        rs.getString("service_name"), 
                        rs.getDouble("price"), 
                        rs.getInt("duration_in_hour"),
                        rs.getString("service_description"),
                        rs.getString("image_url") // Added image_url
                    ));
                }
            }
        }
        return services;
    }

    public Service getServiceById(int serviceId) throws SQLException, ClassNotFoundException {
        String selectSQL = "SELECT * FROM service WHERE service_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Service(
                        rs.getInt("service_id"), 
                        rs.getInt("category_id"), 
                        rs.getInt("status_id"),
                        rs.getString("service_name"), 
                        rs.getDouble("price"), 
                        rs.getInt("duration_in_hour"),
                        rs.getString("service_description"),
                        rs.getString("image_url") // Added image_url
                    );
                }
            }
        }
        return null;
    }
}