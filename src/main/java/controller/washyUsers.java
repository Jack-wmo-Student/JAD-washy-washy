package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user;
import utils.sessionUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class washyUsers extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database environment variables
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_CLASS = System.getenv("DB_CLASS");

    public washyUsers() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check database credentials
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null || DB_CLASS == null) {
            System.err.println("Database credentials or DB_CLASS are missing!");
            throw new ServletException("Database credentials or DB_CLASS are not set in the environment variables.");
        }

        // Load database driver
        try {
            Class.forName(DB_CLASS);
            System.out.println("Database driver loaded successfully: " + DB_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load database driver: " + DB_CLASS);
            throw new ServletException("Database driver not found.", e);
        }

        HttpSession session = request.getSession(false);
        
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null || session.getAttribute("userId") == null) {
        	// Handle invalid login
        	request.setAttribute("error", "You must log in first.");
        	request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }
        
        // Check if user list is already in session
        @SuppressWarnings("unchecked")
        Map<String, List<user>> userStatusMap = (Map<String, List<user>>) session.getAttribute("userStatusMap");

        if (userStatusMap == null) {
            // Fetch users if not already in session
            userStatusMap = fetchUsers();
            session.setAttribute("userStatusMap", userStatusMap);
        }

        // Forward to JSP for display
        request.getRequestDispatcher("/CreateCategoryServlet").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Delegate POST to GET
    }

    private Map<String, List<user>> fetchUsers() {
        String userQuery = "SELECT user_id, username, is_admin, is_blocked FROM users";
        Map<String, List<user>> userStatusMap = new LinkedHashMap<>();

        // Initialize lists for different user statuses
        List<user> adminUsers = new ArrayList<>();
        List<user> regularUsers = new ArrayList<>();
        List<user> blockedUsers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database successfully.");

            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 ResultSet rs = userStmt.executeQuery()) {

                while (rs.next()) {
                    user user = new user(
                            rs.getInt("user_id"),
                            rs.getString("username") != null ? rs.getString("username").trim() : null,
                            rs.getBoolean("is_admin"),
                            rs.getBoolean("is_blocked")
                    );

                    // Categorize users based on their status
                    if (user.isBlocked()) {
                        blockedUsers.add(user);
                    } else if (user.isAdmin()) {
                        adminUsers.add(user);
                    } else {
                        regularUsers.add(user);
                    }
                }
            }

            // Organize users into the map
            userStatusMap.put("adminUsers", adminUsers);
            userStatusMap.put("regularUsers", regularUsers);
            userStatusMap.put("blockedUsers", blockedUsers);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userStatusMap;
    }
}