package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import MODEL.CLASS.User;
import MODEL.DAO.UserDAO;
import utils.passwordUtils;

public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AuthController() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Hash the password
        String hashedPassword = passwordUtils.hashPassword(password);

        // Validate the user using UserDAO
        UserDAO userDAO = new UserDAO();
        User validatedUser = userDAO.validateUser(username, hashedPassword);

        if (validatedUser != null) {
            if (validatedUser.isIsBlocked()) {
                // Handle blocked user
                request.setAttribute("error", "Your account is blocked. Please contact support.");
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            } else {
                // Set a lightweight cookie for session validation
                Cookie isLoggedInCookie = new Cookie("isLoggedIn", "true");
                isLoggedInCookie.setPath("/"); // Cookie is valid for the entire domain
                isLoggedInCookie.setHttpOnly(true); // Prevent JavaScript access
                isLoggedInCookie.setSecure(false); // Ensure it's sent only over HTTPS
                isLoggedInCookie.setMaxAge(60 * 60); // Cookie expiry: 1 hour

                response.addCookie(isLoggedInCookie);

                // Use session attributes for sensitive user data
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", validatedUser);

                // Redirect to homePage
                response.sendRedirect(request.getContextPath() + "/pages/homePage.jsp");
            }
        } else {
            // Handle invalid login
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }
}

