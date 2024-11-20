package backend;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class categoryService extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Environment variables for database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public categoryService() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check for null environment variables
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            System.err.println("Database credentials are missing!");
            throw new ServletException("Database credentials are not set in the environment variables.");
        }

        System.out.println("DB_URL: " + DB_URL);
        System.out.println("DB_USER: " + DB_USER);

        HttpSession session = request.getSession();

        // Check if categories and services are already in session
        @SuppressWarnings("unchecked")
        Map<category, List<service>> categoryServiceMap =
                (Map<category, List<service>>) session.getAttribute("categoryServiceMap");

        if (categoryServiceMap == null) {
            // If not available, fetch from the database and store in session
            categoryServiceMap = fetchCategoriesAndServices();
            session.setAttribute("categoryServiceMap", categoryServiceMap);
            System.out.println("Categories and services stored in session.");
        } else {
            System.out.println("Using categories and services from session.");
        }

        // Redirect to homePage.jsp
        response.sendRedirect(request.getContextPath() + "/pages/homePage.jsp");
    }

    private Map<category, List<service>> fetchCategoriesAndServices() {
        String categoryQuery = "SELECT category_id, category_name, category_description FROM category";
        String serviceQuery = "SELECT service_id, category_id, service_name, price, duration_in_hour, service_description FROM service";
        Map<category, List<service>> categoryServiceMap = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database successfully.");

            // Map to hold categories by ID
            Map<Integer, category> categoryMap = new HashMap<>();

            // Fetch all categories
            try (PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
                 ResultSet categoryRs = categoryStmt.executeQuery()) {

                System.out.println("Fetching categories...");
                while (categoryRs.next()) {
                    category cat = new category(
                            categoryRs.getInt("category_id"),
                            categoryRs.getString("category_name") != null ? categoryRs.getString("category_name").trim() : null,
                            categoryRs.getString("category_description") != null ? categoryRs.getString("category_description").trim() : null
                    );
                    categoryMap.put(cat.getId(), cat); // Store category by ID
                    categoryServiceMap.put(cat, new ArrayList<>()); // Initialize with an empty list
                }
                System.out.println("Total categories fetched: " + categoryMap.size());
            }

            // Fetch all services and link to their categories
            try (PreparedStatement serviceStmt = conn.prepareStatement(serviceQuery);
                 ResultSet serviceRs = serviceStmt.executeQuery()) {

                System.out.println("Fetching services...");
                int serviceCount = 0;
                while (serviceRs.next()) {
                    service serv = new service(
                            serviceRs.getInt("service_id"),
                            serviceRs.getInt("category_id"),
                            serviceRs.getString("service_name") != null ? serviceRs.getString("service_name").trim() : null,
                            serviceRs.getDouble("price"),
                            serviceRs.getInt("duration_in_hour"),
                            serviceRs.getString("service_description") != null ? serviceRs.getString("service_description").trim() : null
                    );

                    // Add service to the corresponding category
                    category cat = categoryMap.get(serv.getCategoryId());
                    if (cat != null) {
                        categoryServiceMap.get(cat).add(serv);
                        serviceCount++;
                    } else {
                        System.err.println("No matching category found for service: " + serv.getName());
                    }
                }
                System.out.println("Total services fetched: " + serviceCount);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while fetching categories and services.");
            e.printStackTrace();
        }

        return categoryServiceMap;
    }
}
