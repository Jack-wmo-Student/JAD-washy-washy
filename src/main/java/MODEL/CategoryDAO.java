package model;
import java.util.List;

public interface CategoryDAO {
    int createCategory(category category) throws DAOException;
    void updateCategory(category category) throws DAOException;
    category getCategory(int categoryId) throws DAOException;
    List<category> getAllCategories() throws DAOException;
    void deleteCategory(int categoryId) throws DAOException;
}
