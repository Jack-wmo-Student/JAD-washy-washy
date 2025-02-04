package CONTROLLER;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import MODEL.CLASS.User;

public class BlockUserServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int userId = Integer.parseInt(request.getParameter("userId"));
		String dbUrl = System.getenv("DB_URL");
		String dbUser = System.getenv("DB_USER");
		String dbPassword = System.getenv("DB_PASSWORD");

		try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
			// Toggle the isBlocked status for the user
			String updateSql = "UPDATE users SET is_blocked = NOT is_blocked WHERE user_id = ?";
			try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
				statement.setInt(1, userId);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Fetch updated user list
		ArrayList<User> users = new ArrayList<User>();
		try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
			String selectSql = "SELECT user_id, username, is_admin, is_blocked FROM users";
			try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					User user = new User();
					user.setUserId(resultSet.getInt("user_id"));
					user.setUsername(resultSet.getString("username"));
					user.setIsAdmin(resultSet.getInt("role_id"));
					user.setIsBlocked(resultSet.getInt("status_id"));
					users.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Set updated user list as a request attribute and forward to JSP
		request.setAttribute("users", users);
		response.sendRedirect(request.getContextPath() + "/UserServlet");
	}
}