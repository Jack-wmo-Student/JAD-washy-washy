package CONTROLLER;

import java.io.IOException;
import java.util.List;

import MODEL.CLASS.User;
import MODEL.DAO.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("UserController doGet method called");

            // Check if user is logged in and is admin
            if (!validateAdminAccess(request, response)) {
                System.out.println("Admin access validation failed");
                return;
            }

            // Get the current user from session
            HttpSession session = request.getSession(false);
            User userObject = null;
            if (session != null) {
                userObject = (User) session.getAttribute("currentUser");
            }

            // Redirect to login if no user found
            if (userObject == null) {
                System.out.println("No user found in session");
                request.setAttribute("error", "Please log in to access this page");
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                return;
            }

            // Get all users and forward to JSP
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

        try {
            // Get the current user and target user IDs
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("currentUser");
            int targetUserId = Integer.parseInt(request.getParameter("userId"));
            String action = request.getParameter("action");

            // Handle different actions
            String message = null;
            switch (action) {
                case "toggle-block":
                    userDAO.toggleUserBlock(targetUserId, currentUser.getUserId());
                    message = "User block status updated successfully";
                    break;
                    
                case "toggle-admin":
                    userDAO.toggleAdminStatus(targetUserId, currentUser.getUserId());
                    message = "User admin status updated successfully";
                    break;
                    
                case "update":
                    handleUpdateUser(request, response);
                    return; // handleUpdateUser handles its own response
                    
                default:
                    request.setAttribute("error", "Invalid action");
                    break;
            }

            // Set success message if action was successful
            if (message != null) {
                request.setAttribute("success", message);
            }

            // Refresh user list and redisplay the page
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID format");
            request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                request.setAttribute("error", "Username cannot be empty");
                request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
                return;
            }

            // Check if username is taken
            if (userDAO.isUsernameTaken(username, userId)) {
                request.setAttribute("error", "Username is already taken");
                request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
                return;
            }

            // Update user
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            userDAO.updateUser(user);
            
            request.setAttribute("success", "User updated successfully");
            
            // Refresh user list and redisplay page
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/pages/memberManagement.jsp").forward(request, response);
        }
    }

    private boolean validateAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
            request.setAttribute("error", "You must log in first.");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return false;
        }

        if (!sessionUtils.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
            return false;
        }

        return true;
    }
}