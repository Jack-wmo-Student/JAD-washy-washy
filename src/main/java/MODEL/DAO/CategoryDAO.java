package MODEL.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import MODEL.CLASS.Category;

public class CategoryDAO {
    private final String dbClass;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public CategoryDAO() {
        // Initialize database configuration
        this.dbClass = System.getenv("DB_CLASS");
        this.dbUrl = System.getenv("DB_URL");
        this.dbUser = System.getenv("DB_USER");
        this.dbPassword = System.getenv("DB_PASSWORD");
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(dbClass);
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public int createCategory(String categoryName, String categoryDescription) throws SQLException, ClassNotFoundException {
        String insertSQL = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
        int generatedId = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, categoryName);
            ps.setString(2, categoryDescription);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        }
        return generatedId;
    }

    public boolean deleteCategory(int categoryId) throws SQLException, ClassNotFoundException {
        String deleteSQL = "DELETE FROM category WHERE category_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
            
            ps.setInt(1, categoryId);
            int rowsDeleted = ps.executeUpdate();
            
            return rowsDeleted > 0;
        }
    }

    public Category getCategoryById(int categoryId) throws SQLException, ClassNotFoundException {
        String selectSQL = "SELECT * FROM category WHERE category_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            
            ps.setInt(1, categoryId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    );
                }
            }
        }
        return null;
    }

    public List<Category> getAllCategories() throws SQLException, ClassNotFoundException {
        List<Category> categories = new ArrayList<>();
        String selectSQL = "SELECT * FROM category";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("category_description")
                ));
            }
        }
        return categories;
    }
}