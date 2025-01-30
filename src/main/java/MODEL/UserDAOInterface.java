package model;

import java.util.*;

public interface UserDAO {
    List<user> getAllUsers() throws DAOException;
    user getUserById(int userId) throws DAOException;
    void toggleUserBlock(int userId, int currentUserId) throws DAOException;
    void toggleAdminStatus(int userId, int currentUserId) throws DAOException;
//    void updateUser(user user) throws DAOException;
    boolean isUsernameTaken(String username, int excludeUserId) throws DAOException;
}

