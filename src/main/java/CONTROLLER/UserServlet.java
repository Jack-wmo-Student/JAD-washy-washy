package CONTROLLER;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import MODEL.CLASS.User;

public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbClass = System.getenv("DB_CLASS");

        ArrayList<User> users = new ArrayList<>();
        try {
            Class.forName(dbClass); // Load database driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database driver not found.");
        }
        
        
        if (request.getAttribute("users") == null) {
        	try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                String sql = "SELECT user_id, username, is_admin, is_blocked FROM users";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
                request.setAttribute("users", users);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to fetch users from the database.");
            }
        } 
        
        // Debugging: Print users count
        System.out.println("Users fetched: " + users.size());      

        // Forward the request to the JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
        dispatcher.forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    	RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
    }
}