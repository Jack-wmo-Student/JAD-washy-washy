package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.passwordUtils;

public class landingPage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Environment variables for database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public landingPage() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        String hashedPassword = passwordUtils.hashPassword(password);
        // Validate the user
        user validatedUser = validateUser(username, hashedPassword);

        if (validatedUser != null) {
            if (validatedUser.isBlocked()) {
                // Handle blocked user
                request.setAttribute("error", "Your account is blocked. Please contact support.");
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            } else {
                // Set a lightweight cookie for session validation
                Cookie isLoggedInCookie = new Cookie("isLoggedIn", "true");
                isLoggedInCookie.setHttpOnly(true); // Prevent JavaScript access
                isLoggedInCookie.setSecure(true); // Ensure it's sent only over HTTPS
                isLoggedInCookie.setMaxAge(60 * 60); // Cookie expiry: 1 hour

                response.addCookie(isLoggedInCookie);

                // Use session attributes for sensitive user data
                HttpSession session = request.getSession();
                session.setAttribute("userId", validatedUser.getId());
                session.setAttribute("username", validatedUser.getUsername());
                session.setAttribute("isAdmin", validatedUser.isAdmin());

                // Redirect to categoryService
                response.sendRedirect(request.getContextPath() + "/pages/homePage.jsp");
            }
        } else {
            // Handle invalid login
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }

    private user validateUser(String username, String password) {
        String DB_CLASS = System.getenv("DB_CLASS");
        try {
            Class.forName(DB_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String query = "SELECT user_id, username, password, is_admin, is_blocked FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        user user = null;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            System.out.println("Executing query: " + query);
            System.out.println("Parameters: Username = " + username + ", Password = " + password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User found in database: " + rs.getString("username"));
                    int userId = rs.getInt("user_id");
                    boolean isAdmin = rs.getBoolean("is_admin") && !rs.wasNull();
                    boolean isBlocked = rs.getBoolean("is_blocked") || rs.wasNull();
                    user = new user(userId, username, isAdmin, isBlocked);

                    System.out.println("is Admin = " + isAdmin + ", is Blocked = " + isBlocked);
                } else {
                    System.out.println("No matching user found in database.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
