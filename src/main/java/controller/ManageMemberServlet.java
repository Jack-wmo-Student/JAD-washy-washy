package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Define Member model class
class Member {
    private int id;
    private String username;
    private boolean isAdmin;
    private boolean isBlocked;

    public Member(int id, String username, boolean isAdmin, boolean isBlocked) {
        this.id = id;
        this.username = username;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public boolean isAdmin() { return isAdmin; }
    public boolean isBlocked() { return isBlocked; }
}

public class ManageMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Environment variables for database credentials
    private static final String DB_CLASS = System.getenv("DB_CLASS");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // List to store member data
        List<Member> members = new ArrayList<>();

        try {
            // Step 1: Load JDBC Driver
            Class.forName(DB_CLASS);

            // Step 2: Establish connection
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Step 3: Execute SQL query
                String sqlStr = "SELECT * FROM users";
                try (ResultSet rs = stmt.executeQuery(sqlStr)) {
                    while (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String username = rs.getString("username");
                        boolean isAdmin = rs.getBoolean("is_admin");
                        boolean isBlocked = rs.getBoolean("is_blocked");

                        // Add member to the list
                        members.add(new Member(userId, username, isAdmin, isBlocked));
                    }
                }
            }
        } catch (Exception e) {
            // Log error and forward to JSP with error message
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
        }

        // Set members list as request attribute
        request.setAttribute("members", members);

        // Forward request to JSP
        request.getRequestDispatcher("/pages/memberList.jsp").forward(request, response);
    }
}