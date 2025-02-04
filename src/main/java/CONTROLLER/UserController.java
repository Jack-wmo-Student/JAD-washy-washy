package CONTROLLER;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import MODEL.CLASS.User;
import MODEL.DAO.UserDAO;
import utils.sessionUtils;
import utils.passwordUtils;
import java.util.*;

@WebServlet("/user/*")
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Validate admin access
            if (!validateAdminAccess(request, response)) {
                return;
            }

            // Get all users
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            
            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error retrieving users: " + e.getMessage());
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Validate admin access
        if (!validateAdminAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendJsonResponse(response, 400, "Invalid request path");
            return;
        }

        try {
            HttpSession session = request.getSession(false);
            int currentUserId = (int) session.getAttribute("userId");
            int targetUserId = Integer.parseInt(request.getParameter("userId"));

            switch (pathInfo) {
                case "/toggle-block":
                    handleToggleBlock(targetUserId, currentUserId, response);
                    break;
                    
                case "/toggle-admin":
                    handleToggleAdmin(targetUserId, currentUserId, response);
                    break;
                    
                case "/update":
                    handleUpdateUser(request, response);
                    break;
                    
                default:
                    sendJsonResponse(response, 400, "Invalid action");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(response, 400, "Invalid user ID format");
        } catch (Exception e) {
            sendJsonResponse(response, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleToggleBlock(int targetUserId, int currentUserId, HttpServletResponse response) 
            throws IOException {
        try {
            userDAO.toggleUserBlock(targetUserId, currentUserId);
            sendJsonResponse(response, 200, "User block status updated successfully");
        } catch (Exception e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private void handleToggleAdmin(int targetUserId, int currentUserId, HttpServletResponse response) 
            throws IOException {
        try {
            userDAO.toggleAdminStatus(targetUserId, currentUserId);
            sendJsonResponse(response, 200, "User admin status updated successfully");
        } catch (Exception e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Extract parameters
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                sendJsonResponse(response, 400, "Username cannot be empty");
                return;
            }

            if (password != null && !password.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    sendJsonResponse(response, 400, "Passwords do not match");
                    return;
                }
            }

            // Check if username is taken
            if (userDAO.isUsernameTaken(username, userId)) {
                sendJsonResponse(response, 400, "Username is already taken");
                return;
            }

            // Update user
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
//            if (password != null && !password.isEmpty()) {
//                user.setPassword(passwordUtils.hashPassword(password));
//            }

            userDAO.updateUser(user);
            sendJsonResponse(response, 200, "User updated successfully");

        } catch (Exception e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private boolean validateAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Check if user is logged in
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, 401, "You must be logged in");
            } else {
                request.setAttribute("error", "You must log in first.");
                try {
                    request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        // Check if user is admin
        if (!sessionUtils.isAdmin(request)) {
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, 403, "Admin access required");
            } else {
                response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
            }
            return false;
        }

        return true;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private void sendJsonResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        
        // Simple JSON string construction
        String jsonResponse = String.format("{\"message\": \"%s\"}", 
            message.replace("\"", "\\\"")  // Escape quotes in message
        );
        
        response.getWriter().write(jsonResponse);
    }
}