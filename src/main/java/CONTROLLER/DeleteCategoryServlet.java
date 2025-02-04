package CONTROLLER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;
import java.util.Map;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;

import java.util.List;

public class DeleteCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_CLASS = System.getenv("DB_CLASS"); 
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	// Check if the user is logged in
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn")) {
        	// Handle invalid login
        	request.setAttribute("error", "You must log in first.");
        	request.getRequestDispatcher("/pages/index.jsp").forward(request, response);
            return;
        }

        // Optional: Check if the user is an admin
        if (!sessionUtils.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/pages/forbidden.jsp");
            return;
        }
    	
        Connection conn = null;
        PreparedStatement ps = null;

        // Get the category ID from the request
        String categoryId = request.getParameter("categoryId");
        
		HttpSession session = request.getSession(false);
        
        if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null || session.getAttribute("userId") == null) {
        	// Handle invalid login
        	request.setAttribute("error", "You must log in first.");
        	request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }

        try {
            // Load database driver
            Class.forName(DB_CLASS);

            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL to delete the category
            String deleteSQL = "DELETE FROM category WHERE category_id = ?";
            ps = conn.prepareStatement(deleteSQL);
            ps.setInt(1, Integer.parseInt(categoryId));
            
            // Execute the delete query
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                // Update the session attribute to reflect the deletion
                @SuppressWarnings("unchecked")
                Map<Category, List<Service>> sessionCategoryServiceMap = 
                        (Map<Category, List<Service>>) session.getAttribute("categoryServiceMap");

                if (sessionCategoryServiceMap != null) {
                    // Locate and remove the category
                    Category toRemove = null;
                    for (Category cat : sessionCategoryServiceMap.keySet()) {
                        if (cat.getId() == Integer.parseInt(categoryId)) {
                            toRemove = cat;
                            break;
                        }
                    }

                    if (toRemove != null) {
                        sessionCategoryServiceMap.remove(toRemove);
                    }

                    // Update the session attribute
                    session.setAttribute("categoryServiceMap", sessionCategoryServiceMap);
                }

                // Set a success message
                request.setAttribute("message", "Category deleted successfully!");
            } else {
                request.setAttribute("message", "Category not found or could not be deleted.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error deleting category: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Forward the request back to the categories page with a message
        response.sendRedirect(request.getContextPath() + "/CreateCategoryServlet");
    }
}