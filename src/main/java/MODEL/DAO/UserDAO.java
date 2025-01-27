package MODEL.DAO;

import MODEL.CLASS.*;
import DBACCESS.DBConnection;
import java.sql.*;

/**
 * 
 * This is a utility Bean for extracting user info from the DB and populate the
 * value Bean
 */

public class UserDAO {

	public User validateUser(String user_name, String password) {
		User uBean = null;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			String sqlStr = "SELECT user_id, username, password, is_admin, is_blocked FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
			PreparedStatement pstmt = conn.prepareStatement(sqlStr);
			pstmt.setString(1, user_name);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int user_id = rs.getInt("user_id");
					boolean is_admin = rs.getBoolean("is_admin") && !rs.wasNull();
					boolean is_blocked = rs.getBoolean("is_blocked") || rs.wasNull();
					uBean = new User(user_id, user_name, is_admin, is_blocked);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return uBean;
	}

//	public User getUserDetails(String user_id) throws SQLException {
//
//		User uBean = null;
//		Connection conn = null;
//		try {
//			conn = DBConnection.getConnection();
//			String sqlStr = "SELECT * FROM users WHERE user_id = ?";
//			PreparedStatement pstmt = conn.prepareStatement(sqlStr);
//			pstmt.setString(1, user_id);
//			ResultSet rs = pstmt.executeQuery();
//			if (rs.next()) {
//				uBean = new User();
//				uBean.setUserId(Integer.parseInt(rs.getString("user_id")));
//				uBean.setAge(rs.getInt("age"));
//				uBean.setGender(rs.getString("gender"));
//				System.out.print(" ..... done writing to bean !...... ");
//			}
//		} catch (Exception e) {
//			System.out.print("------------UserDetailsDB:" + e);
//		} finally {
//			conn.close();
//		}
//		return uBean;
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
}
