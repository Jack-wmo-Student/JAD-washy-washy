package MODEL.DAO;

import MODEL.CLASS.*;
import DBACCESS.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This is a utility Bean for extracting user info from the DB and populate the
 * value Bean
 */

public class UserDAO {

	public User validateUser(String user_name, String password) {
		User user = null;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			String sqlStr = "SELECT user_id, status_id, role_id, username, password FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
			PreparedStatement pstmt = conn.prepareStatement(sqlStr);
			pstmt.setString(1, user_name);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int user_id = rs.getInt("user_id");
					try {
						int is_admin = rs.getInt("role_id");
						int is_blocked = rs.getInt("status_id");
						user = new User(user_id, user_name, is_admin, is_blocked);
					} catch (NullPointerException e) {
						throw new Error("Error: invalid user record!!");
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return user;
	}

//	protected User getUserDetails(int user_id) throws SQLException {
//		String sql = "SELECT * FROM users WHERE user_id = ?";
//		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//			ps.setInt(1, user_id);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					return mapResultSetToUser(rs);
//				}
//				throw new SQLException("User not found with ID: " + user_id);
//			}
//		} catch (SQLException e) {
//			throw new SQLException("Error retrieving user", e);
//		}
//	}
//
//	public int insertUser(String userid, int age, String gender) {
//		// UserDetails uBean = null;
//		Connection conn = null;
//		int rowsAffected = 0;
//		try {
//			conn = DBConnection.getConnection();
//			String sqlStr = "INSERT INTO user_details VALUES (?,?,?)";
//			PreparedStatement pstmt = conn.prepareStatement(sqlStr);
//			pstmt.setString(1, userid);
//			pstmt.setInt(2, age);
//			pstmt.setString(3, gender);
//			rowsAffected = pstmt.executeUpdate();
//			System.out.println("Number of rows inserted: " + rowsAffected);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return rowsAffected;
//	}
//}

	public List<User> getAllUsers() throws SQLException {
		String sql = "SELECT user_id, username, is_admin, is_blocked FROM users ORDER BY username";
		List<User> users = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					User user = mapResultSetToUser(rs);
					users.add(user);
				}
			}
			return users;
		} catch (SQLException e) {
			throw new SQLException("Error retrieving users", e);
		}
	}

	public User getUserById(int userId) throws SQLException {
		String sql = "SELECT user_id, username, status_id, role_id FROM users WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToUser(rs);
				}
				throw new SQLException("User not found with ID: " + userId);
			}
		} catch (SQLException e) {
			throw new SQLException("Error retrieving user", e);
		}
	}

	public void toggleUserBlock(int userId, int currentUserId) throws SQLException {
		// Check if trying to block self
		if (userId == currentUserId) {
			throw new SQLException("You cannot block yourself.");
		}

		// Check if target user is an admin
		User targetUser = getUserById(userId);
		if (targetUser.isIsAdmin()) {
			throw new SQLException("You cannot block an administrator.");
		}

		String sql = "UPDATE users SET is_blocked = NOT is_blocked WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				throw new SQLException("User not found with ID: " + userId);
			}
		} catch (SQLException e) {
			throw new SQLException("Error toggling user block status", e);
		}
	}

	public void toggleAdminStatus(int userId, int currentUserId) throws SQLException {
		// Check if trying to change own admin status
		if (userId == currentUserId) {
			throw new SQLException("You cannot modify your own admin status.");
		}

		// Check if user is blocked
		User targetUser = getUserById(userId);
		if (targetUser.isIsBlocked()) {
			throw new SQLException("Cannot modify admin status of a blocked user.");
		}

		String sql = "UPDATE users SET is_admin = NOT is_admin WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				throw new SQLException("User not found with ID: " + userId);
			}
		} catch (SQLException e) {
			throw new SQLException("Error toggling admin status", e);
		}
	}

	public void updateUser(User user) throws SQLException {
		String sql = "UPDATE users SET username = ?, WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, user.getUsername());
			ps.setInt(3, user.getUserId());

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected == 0) {
				throw new SQLException("User not found with ID: " + user.getUserId());
			}
		} catch (SQLException e) {
			throw new SQLException("Error updating user", e);
		}
	}

	public boolean isUsernameTaken(String username, int excludeUserId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?) AND user_id != ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			ps.setInt(2, excludeUserId);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new SQLException("Error checking username availability", e);
		}
	}

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setUsername(rs.getString("username"));
		user.setIsAdmin(rs.getInt("status_id"));
		user.setIsBlocked(rs.getInt("role_id"));
		return user;
	}
}
