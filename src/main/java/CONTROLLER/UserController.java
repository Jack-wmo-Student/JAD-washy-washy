package CONTROLLER;

import java.io.IOException;
import java.util.List;

import MODEL.CLASS.User;
import MODEL.DAO.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

 // In UserController.java
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("UserController doGet method called");
        try {
            if (!validateAdminAccess(request, response)) {
                return;
            }

            List<User> users = userDAO.getAllUsers();
            System.out.println("Retrieved " + users.size() + " users");
            request.setAttribute("users", users);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            System.out.println("Error in UserController: " + e.getMessage());
            e.printStackTrace();
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
                    userDAO.toggleUserBlock(targetUserId, currentUserId);
                    sendJsonResponse(response, 200, "User block status updated successfully");
                    break;
                    
                case "/toggle-admin":
                    userDAO.toggleAdminStatus(targetUserId, currentUserId);
                    sendJsonResponse(response, 200, "User admin status updated successfully");
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

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                sendJsonResponse(response, 400, "Username cannot be empty");
                return;
            }

            // Check if username is taken
            if (userDAO.isUsernameTaken(username, userId)) {
                sendJsonResponse(response, 400, "Username is already taken");
                return;
            }

            // Update user using DAO
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            userDAO.updateUser(user);
            
            sendJsonResponse(response, 200, "User updated successfully");
        } catch (Exception e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private boolean validateAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
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
        response.getWriter().write(String.format("{\"message\": \"%s\"}", 
            message.replace("\"", "\\\"")));
    }
}