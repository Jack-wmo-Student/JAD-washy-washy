package MODEL.DAO;

import DBACCESS.DBConnection;
import MODEL.CLASS.*;

import java.sql.*;
import java.util.*;

public class CategoryServiceDAO {

	public static Map<Category, List<Service>> fetchCategoriesAndServices() throws SQLException{
		String categoryQuery = "SELECT category_id, category_name, category_description FROM category";
		String serviceQuery = "SELECT service_id, category_id, status_id, service_name, price, duration_in_hour, service_description FROM service";
		Map<Category, List<Service>> categoryServiceMap = new LinkedHashMap<>();

		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null) {
				throw new SQLException("Failed to establish database connection.");
			}
			System.out.println("Connected to the database successfully.");

			Map<Integer, Category> categoryMap = new LinkedHashMap<>();

			// Fetch categories
			try (PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
					ResultSet categoryRs = categoryStmt.executeQuery()) {

				while (categoryRs.next()) {
					Category cat = new Category(categoryRs.getInt("category_id"),
							categoryRs.getString("category_name") != null ? categoryRs.getString("category_name").trim()
									: null,
							categoryRs.getString("category_description") != null
									? categoryRs.getString("category_description").trim()
									: null);
					categoryMap.put(cat.getId(), cat);
					categoryServiceMap.put(cat, new ArrayList<>());
				}
			}

			// Fetch services and link to categories
			try (PreparedStatement serviceStmt = conn.prepareStatement(serviceQuery);
					ResultSet serviceRs = serviceStmt.executeQuery()) {

				while (serviceRs.next()) {
					Service serv = new Service(serviceRs.getInt("service_id"), serviceRs.getInt("category_id"),
							serviceRs.getInt("status_id"),
							serviceRs.getString("service_name") != null ? serviceRs.getString("service_name").trim()
									: null,
							serviceRs.getDouble("price"), serviceRs.getInt("duration_in_hour"),
							serviceRs.getString("service_description") != null
									? serviceRs.getString("service_description").trim()
									: null);
					if (serv.getStatusId() == 4) {
						Category cat = categoryMap.get(serv.getCategoryId());
						if (cat != null) {
							categoryServiceMap.get(cat).add(serv);
						}
					} else {
						continue;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return categoryServiceMap;
		}
	}
}