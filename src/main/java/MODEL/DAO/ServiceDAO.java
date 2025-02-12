package MODEL.DAO;

import MODEL.CLASS.Service;
import utils.CloudinaryUtil;
import DBACCESS.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    
    public int createService(String name, int categoryId, double price, int duration, 
                           String description, int statusId, String imageUrl) throws SQLException {
        String sql = "INSERT INTO service (service_name, category_id, price, duration_in_hour, " +
                    "service_description, status_id, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, categoryId);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, duration);
            pstmt.setString(5, description);
            pstmt.setInt(6, statusId);
            pstmt.setString(7, imageUrl);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateService(int serviceId, String serviceName, double price, 
                               int duration, String description, String imageUrl) throws SQLException {
        String sql = "UPDATE service SET service_name = ?, price = ?, duration_in_hour = ?, " +
                    "service_description = ?, image_url = ? WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceName);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, duration);
            pstmt.setString(4, description);
            pstmt.setString(5, imageUrl);
            pstmt.setInt(6, serviceId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteService(int serviceId) throws SQLException {
        // First, get the image URL to delete from Cloudinary later
        String getImageSql = "SELECT image_url FROM service WHERE service_id = ?";
        String imageUrl = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getImageSql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    imageUrl = rs.getString("image_url");
                }
            }
        }

        // Now proceed with deletion
        String sql = "DELETE FROM service WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            boolean deleted = pstmt.executeUpdate() > 0;
            
            // If deletion was successful and there was an image, delete it from Cloudinary
            if (deleted && imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    String publicId = CloudinaryUtil.getPublicIdFromUrl(imageUrl);
                    CloudinaryUtil.deleteImage(publicId);
                } catch (IOException e) {
                    // Log the error but don't fail the deletion
                    e.printStackTrace();
                }
            }
            return deleted;
        }
    }

    public List<Service> getServicesByCategory(int categoryId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(new Service(
                        rs.getInt("service_id"),
                        rs.getInt("category_id"),
                        rs.getInt("status_id"),
                        rs.getString("service_name"),
                        rs.getDouble("price"),
                        rs.getInt("duration_in_hour"),
                        rs.getString("service_description"),
                        rs.getString("image_url")
                    ));
                }
            }
        }
        return services;
    }
}