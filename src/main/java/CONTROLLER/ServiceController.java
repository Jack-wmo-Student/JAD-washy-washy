package CONTROLLER;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;
import MODEL.DAO.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.sessionUtils;

public class ServiceController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ServiceDAO serviceDAO;

    public ServiceController() {
        this.serviceDAO = new ServiceDAO();
    }
    
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!isAuthorized(request, response)) {
                return;
            }

            String categoryId = request.getParameter("categoryId");
            if (categoryId == null || categoryId.trim().isEmpty()) {
                request.setAttribute("error", "Invalid category ID.");
                request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
                return;
            }

            List<Service> services = serviceDAO.getServicesByCategory(Integer.parseInt(categoryId));
            request.setAttribute("services", services);
            request.setAttribute("categoryId", categoryId);
            request.getRequestDispatcher("/pages/editService.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid category ID format");
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAuthorized(request, response)) {
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        HttpSession session = request.getSession(false);
        String categoryId = request.getParameter("categoryId");
        
        try {
            switch (action) {
                case "/create":
                    handleCreate(request, session);
                    break;
                case "/update":
                    handleUpdate(request, session);
                    break;
                case "/delete":
                    handleDelete(request, session);
                    break;
                default:
                    session.setAttribute("errorMessage", "Invalid action specified");
                    break;
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            e.printStackTrace();
        }

        // Improved redirect logic
        if (categoryId != null) {
            response.sendRedirect(request.getContextPath() + "/pages/editService.jsp?categoryId=" + categoryId);
        } else {
            response.sendRedirect(request.getContextPath() + "/pages/error.jsp");
        }
    }

    private void handleCreate(HttpServletRequest request, HttpSession session) throws Exception {
        // Adding validation to prevent null pointer exceptions
        if (request == null || session == null) {
            throw new IllegalArgumentException("Request or session cannot be null");
        }

        // Get and validate required parameters
        String serviceName = request.getParameter("serviceName");
        String categoryIdStr = request.getParameter("categoryId");
        String servicePriceStr = request.getParameter("servicePrice");
        String serviceDurationStr = request.getParameter("serviceDuration");
        String description = request.getParameter("serviceDescription");

        // Validate all required fields are present
        if (serviceName == null || categoryIdStr == null || servicePriceStr == null || 
            serviceDurationStr == null || description == null) {
            session.setAttribute("errorMessage", "All fields are required");
            return;
        }

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            double price = Double.parseDouble(servicePriceStr);
            int duration = Integer.parseInt(serviceDurationStr);
            int status_id = 4;

            int generatedId = serviceDAO.createService(serviceName, categoryId, price, duration, description, status_id);

            if (generatedId > 0) {
                updateSessionServiceMap(session, categoryId);
                session.setAttribute("successMessage", "Service added successfully!");
                session.setAttribute("categoryId", categoryId); // Store for redirect
            } else {
                session.setAttribute("errorMessage", "Failed to add service");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid number format in input fields");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error creating service");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpSession session) throws Exception {
        // Get and validate all parameters
        String serviceIdStr = request.getParameter("serviceId");
        String serviceName = request.getParameter("serviceName");
        String servicePriceStr = request.getParameter("servicePrice");
        String serviceDurationStr = request.getParameter("serviceDuration");
        String description = request.getParameter("serviceDescription");
        String categoryIdStr = request.getParameter("categoryId");

        // Validate that all required fields are present
        if (serviceIdStr == null || serviceName == null || servicePriceStr == null || 
            serviceDurationStr == null || description == null || categoryIdStr == null) {
            session.setAttribute("errorMessage", "All fields are required");
            return;
        }

        try {
            // Parse numeric values
            int serviceId = Integer.parseInt(serviceIdStr);
            double price = Double.parseDouble(servicePriceStr);
            int duration = Integer.parseInt(serviceDurationStr);
            int categoryId = Integer.parseInt(categoryIdStr);

            // Attempt to update the service
            boolean updated = serviceDAO.updateService(serviceId, serviceName, price, duration, description);

            if (updated) {
                // Update the session's service map
                updateSessionServiceMap(session, categoryId);
                session.setAttribute("successMessage", "Service updated successfully!");
                
                // Store parameters for the redirect back to serviceEditor.jsp
                session.setAttribute("editServiceId", serviceId);
                session.setAttribute("editCategoryId", categoryId);
            } else {
                session.setAttribute("errorMessage", "Failed to update service");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid number format in input fields");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error updating service: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpSession session) throws Exception {
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));

        boolean deleted = serviceDAO.deleteService(serviceId);

        if (deleted) {
            updateSessionServiceMap(session, categoryId);
            session.setAttribute("successMessage", "Service deleted successfully!");
        } else {
            session.setAttribute("errorMessage", "Service not found or could not be deleted.");
        }
    }

    private boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
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

    private void updateSessionServiceMap(HttpSession session, int categoryId) throws Exception {
        @SuppressWarnings("unchecked")
        Map<Category, List<Service>> sessionCategoryServiceMap = 
                (Map<Category, List<Service>>) session.getAttribute("categoryServiceMap");

        if (sessionCategoryServiceMap != null) {
            List<Service> updatedServices = serviceDAO.getServicesByCategory(categoryId);
            
            for (Map.Entry<Category, List<Service>> entry : sessionCategoryServiceMap.entrySet()) {
                if (entry.getKey().getId() == categoryId) {
                    entry.setValue(updatedServices);
                    break;
                }
            }
            
            session.setAttribute("categoryServiceMap", sessionCategoryServiceMap);
        }
    }
}