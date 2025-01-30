package model;

import model.user;
import model.DAOException;
import utils.DatabaseUtil;
import java.sql.*;
import java.util.*;


public class UserDAOImpl implements UserDAO {
    @Override
    public List<user> getAllUsers() throws DAOException {
        String sql = "SELECT user_id, username, is_admin, is_blocked FROM users ORDER BY username";
        List<user> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    user user = mapResultSetToUser(rs);
                    users.add(user);
                }
            }
            return users;
        } catch (SQLException e) {
            throw new DAOException("Error retrieving users", e);
        }
    }

    @Override
    public user getUserById(int userId) throws DAOException {
        String sql = "SELECT user_id, username, is_admin, is_blocked FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                throw new DAOException("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving user", e);
        }
    }

    @Override
    public void toggleUserBlock(int userId, int currentUserId) throws DAOException {
        // Check if trying to block self
        if (userId == currentUserId) {
            throw new DAOException("You cannot block yourself.");
        }
        
        // Check if target user is an admin
        user targetUser = getUserById(userId);
        if (targetUser.isAdmin()) {
            throw new DAOException("You cannot block an administrator.");
        }

        String sql = "UPDATE users SET is_blocked = NOT is_blocked WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DAOException("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DAOException("Error toggling user block status", e);
        }
    }

    @Override
    public void toggleAdminStatus(int userId, int currentUserId) throws DAOException {
        // Check if trying to change own admin status
        if (userId == currentUserId) {
            throw new DAOException("You cannot modify your own admin status.");
        }
        
        // Check if user is blocked
        user targetUser = getUserById(userId);
        if (targetUser.isBlocked()) {
            throw new DAOException("Cannot modify admin status of a blocked user.");
        }

        String sql = "UPDATE users SET is_admin = NOT is_admin WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DAOException("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DAOException("Error toggling admin status", e);
        }
    }

//    @Override
//    public void updateUser(user user) throws DAOException {
//        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
//        
//        try (Connection conn = DatabaseUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setString(1, user.getUsername());
//            ps.setString(2, user.getPassword());
//            ps.setInt(3, user.getUser_id());
//            
//            int rowsAffected = ps.executeUpdate();
//            if (rowsAffected == 0) {
//                throw new DAOException("User not found with ID: " + user.getUser_id());
//            }
//        } catch (SQLException e) {
//            throw new DAOException("Error updating user", e);
//        }
//    }

    @Override
    public boolean isUsernameTaken(String username, int excludeUserId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setInt(2, excludeUserId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking username availability", e);
        }
    }

    private user mapResultSetToUser(ResultSet rs) throws SQLException {
        user user = new user();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setAdmin(rs.getBoolean("is_admin"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        return user;
    }
}