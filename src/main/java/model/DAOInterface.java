package model;

import model.category;
import model.service;
import model.user;
import java.util.List;

public interface DAOInterface {
    int createCategory(category category) throws DAOException;
    void updateCategory(category category) throws DAOException;
    category getCategory(int categoryId) throws DAOException;
    List<category> getAllCategories() throws DAOException;
    void deleteCategory(int categoryId) throws DAOException;
}

public interface ServiceDAO {
    void createService(service service) throws DAOException;
    void updateService(service service) throws DAOException;
    service getService(int serviceId) throws DAOException;
    List<service> getServicesByCategory(int categoryId) throws DAOException;
    void deleteService(int serviceId) throws DAOException;
}

public interface UserDAO {
    List<user> getAllUsers() throws DAOException;
    void updateUserStatus(int userId, boolean isBlocked) throws DAOException;
    void updateUserRole(int userId, boolean isAdmin) throws DAOException;
    user getUser(int userId) throws DAOException;
}

public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}