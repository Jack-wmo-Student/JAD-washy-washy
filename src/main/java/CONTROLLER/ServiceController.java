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
        if (!isAuthorized(request, response)) {
            return;
        }

        String categoryId = request.getParameter("categoryId");
        if (categoryId == null || categoryId.isEmpty()) {
            request.setAttribute("errorMessage", "Invalid category ID.");
            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);
            return;
        }

        try {
            List<Service> services = serviceDAO.getServicesByCategory(Integer.parseInt(categoryId));
            request.setAttribute("services", services);
            request.setAttribute("categoryId", categoryId);
            response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching services: " + e.getMessage());
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

        response.sendRedirect(request.getContextPath() + "/pages/editServiceCategory.jsp");
    }

    private void handleCreate(HttpServletRequest request, HttpSession session) throws Exception {
        String serviceName = request.getParameter("serviceName");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        double price = Double.parseDouble(request.getParameter("servicePrice"));
        int duration = Integer.parseInt(request.getParameter("serviceDuration"));
        String description = request.getParameter("serviceDescription");

        int generatedId = serviceDAO.createService(serviceName, categoryId, price, duration, description);

        if (generatedId > 0) {
            updateSessionServiceMap(session, categoryId);
            session.setAttribute("successMessage", "Service added successfully!");
        } else {
            session.setAttribute("errorMessage", "Failed to add service.");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpSession session) throws Exception {
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        String serviceName = request.getParameter("serviceName");
        double price = Double.parseDouble(request.getParameter("servicePrice"));
        int duration = Integer.parseInt(request.getParameter("serviceDuration"));
        String description = request.getParameter("serviceDescription");

        boolean updated = serviceDAO.updateService(serviceId, serviceName, price, duration, description);

        if (updated) {
            updateSessionServiceMap(session, Integer.parseInt(request.getParameter("categoryId")));
            session.setAttribute("successMessage", "Service updated successfully!");
        } else {
            session.setAttribute("errorMessage", "Failed to update service.");
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