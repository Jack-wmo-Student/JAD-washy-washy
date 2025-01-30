package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.UserDAO;
import model.UserDAOImpl;
import model.user;
import model.DAOException;
import utils.sessionUtils;
import utils.passwordUtils;

@WebServlet("/user/*")
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Check if user is admin
            if (!sessionUtils.isAdmin(request)) {
                request.setAttribute("error", "Access denied. Admin privileges required.");
                response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
                return;
            }

            // Get all users
            request.setAttribute("users", userDAO.getAllUsers());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/memberManagement.jsp");
            dispatcher.forward(request, response);

        } catch (DAOException e) {
            handleError(request, response, "Error retrieving users: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
        // Check if user is admin
        if (!sessionUtils.isAdmin(request)) {
            sendJsonResponse(response, 403, "Access denied. Admin privileges required.");
            return;
        }

        try {
            // Get the current admin's ID
            HttpSession session = request.getSession(false);
            int currentUserId = (int) session.getAttribute("userId");
            int targetUserId = Integer.parseInt(request.getParameter("userId"));

            switch (action) {
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
            sendJsonResponse(response, 400, "Invalid user ID");
        } catch (Exception e) {
            sendJsonResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }

    private void handleToggleBlock(int targetUserId, int currentUserId, HttpServletResponse response) 
            throws IOException {
        try {
            userDAO.toggleUserBlock(targetUserId, currentUserId);
            sendJsonResponse(response, 200, "User block status updated successfully");
        } catch (DAOException e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private void handleToggleAdmin(int targetUserId, int currentUserId, HttpServletResponse response) 
            throws IOException {
        try {
            userDAO.toggleAdminStatus(targetUserId, currentUserId);
            sendJsonResponse(response, 200, "User admin status updated successfully");
        } catch (DAOException e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Extract and validate parameters
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
            user user = new user();
            user.setUserId(userId);
            user.setUsername(username);
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordUtils.hashPassword(password));
            }

            userDAO.updateUser(user);
            sendJsonResponse(response, 200, "User updated successfully");

        } catch (DAOException e) {
            sendJsonResponse(response, 400, e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String error) 
            throws ServletException, IOException {
        request.setAttribute("error", error);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/error.jsp");
        dispatcher.forward(request, response);
    }

    private void sendJsonResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}