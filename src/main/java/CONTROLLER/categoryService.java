package CONTROLLER;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;

public class categoryService extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Environment variables for database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_CLASS = System.getenv("DB_CLASS"); // Add DB_CLASS here

    public categoryService() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Delegate POST handling to doGet
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Database credentials check
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null || DB_CLASS == null) {
            System.err.println("Database credentials or DB_CLASS are missing!");
            throw new ServletException("Database credentials or DB_CLASS are not set in the environment variables.");
        }

        // Load database driver
        try {
            Class.forName(DB_CLASS); // Load the database driver
            System.out.println("Database driver loaded successfully: " + DB_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load database driver: " + DB_CLASS);
            throw new ServletException("Database driver not found.", e);
        }

        HttpSession session = request.getSession();

        // Check if categories and services are already in session
        @SuppressWarnings("unchecked")
        Map<Category, List<Service>> categoryServiceMap =
                (Map<Category, List<Service>>) session.getAttribute("categoryServiceMap");

        if (categoryServiceMap == null) {
            // Fetch categories and services if not already in session
            categoryServiceMap = FetchCategoriesAndServices();
            session.setAttribute("categoryServiceMap", categoryServiceMap);
        }

        // Forward to homePage.jsp to display the data
        request.getRequestDispatcher("/pages/homePage.jsp").forward(request, response);
    }

    private Map<Category, List<Service>> FetchCategoriesAndServices() {
        String categoryQuery = "SELECT category_id, category_name, category_description FROM category";
        String serviceQuery = "SELECT service_id, category_id, service_name, price, duration_in_hour, service_description FROM service";
        Map<Category, List<Service>> categoryServiceMap = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database successfully.");

            // Map to hold categories by ID
            Map<Integer, Category> categoryMap = new LinkedHashMap<>();

            // Fetch all categories
            try (PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
                 ResultSet categoryRs = categoryStmt.executeQuery()) {

                while (categoryRs.next()) {
                    Category cat = new Category(
                            categoryRs.getInt("category_id"),
                            categoryRs.getString("category_name") != null ? categoryRs.getString("category_name").trim() : null,
                            categoryRs.getString("category_description") != null ? categoryRs.getString("category_description").trim() : null
                    );
                    categoryMap.put(cat.getId(), cat); // Store category by ID
                    categoryServiceMap.put(cat, new ArrayList<>()); // Initialize with an empty list
                }
            }

            // Fetch all services and link to their categories
            try (PreparedStatement serviceStmt = conn.prepareStatement(serviceQuery);
                 ResultSet serviceRs = serviceStmt.executeQuery()) {

                while (serviceRs.next()) {
                    Service serv = new Service(
                            serviceRs.getInt("service_id"),
                            serviceRs.getInt("category_id"),
                            serviceRs.getString("service_name") != null ? serviceRs.getString("service_name").trim() : null,
                            serviceRs.getDouble("price"),
                            serviceRs.getInt("duration_in_hour"),
                            serviceRs.getString("service_description") != null ? serviceRs.getString("service_description").trim() : null
                    );

                    // Add service to the corresponding category
                    Category cat = categoryMap.get(serv.getCategoryId());
                    if (cat != null) {
                        categoryServiceMap.get(cat).add(serv);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryServiceMap;
    }
}
