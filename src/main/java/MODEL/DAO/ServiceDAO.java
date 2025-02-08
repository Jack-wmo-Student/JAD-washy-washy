package MODEL.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import MODEL.CLASS.Service;

public class ServiceDAO {
	private final String dbClass;
	private final String dbUrl;
	private final String dbUser;
	private final String dbPassword;

	public ServiceDAO() {
		this.dbClass = System.getenv("DB_CLASS");
		this.dbUrl = System.getenv("DB_URL");
		this.dbUser = System.getenv("DB_USER");
		this.dbPassword = System.getenv("DB_PASSWORD");
	}

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(dbClass);
		return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	}

	public int createService(String serviceName, int categoryId, double price, int duration, String description)
			throws SQLException, ClassNotFoundException {
		String insertSQL = "INSERT INTO service (service_name, category_id, price, duration_in_hour, service_description) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, serviceName);
			ps.setInt(2, categoryId);
			ps.setDouble(3, price);
			ps.setInt(4, duration);
			ps.setString(5, description);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		}
		return 0;
	}

	public boolean updateService(int serviceId, String serviceName, double price, int duration, String description)
			throws SQLException, ClassNotFoundException {
		String updateSQL = "UPDATE service SET service_name = ?, price = ?, "
				+ "duration_in_hour = ?, service_description = ? WHERE service_id = ?";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(updateSQL)) {

			ps.setString(1, serviceName);
			ps.setDouble(2, price);
			ps.setInt(3, duration);
			ps.setString(4, description);
			ps.setInt(5, serviceId);

			return ps.executeUpdate() > 0;
		}
	}

	public boolean deleteService(int serviceId) throws SQLException, ClassNotFoundException {
	    // SQL to delete associated service timeslots
	    String deleteServiceTimeslotsSQL = "DELETE FROM service_timeslot WHERE service_id = ?";
	    
	    // SQL to delete associated bookings
	    String deleteBookingsSQL = "DELETE FROM booking WHERE service_id = ?";
	    
	    // SQL to delete the service itself
	    String deleteServiceSQL = "DELETE FROM service WHERE service_id = ?";

	    Connection conn = null;
	    try {
	        conn = getConnection();
	        // Disable auto-commit to ensure transaction integrity
	        conn.setAutoCommit(false);

	        // First, delete associated service timeslots
	        try (PreparedStatement psServiceTimeslots = conn.prepareStatement(deleteServiceTimeslotsSQL)) {
	            psServiceTimeslots.setInt(1, serviceId);
	            int serviceTimeslotsDeleted = psServiceTimeslots.executeUpdate();
	            System.out.println("Number of service timeslots deleted: " + serviceTimeslotsDeleted);
	        }

	        // Next, delete associated bookings
	        try (PreparedStatement psBookings = conn.prepareStatement(deleteBookingsSQL)) {
	            psBookings.setInt(1, serviceId);
	            int bookingsDeleted = psBookings.executeUpdate();
	            System.out.println("Number of bookings deleted: " + bookingsDeleted);
	        }

	        // Finally, delete the service itself
	        try (PreparedStatement psService = conn.prepareStatement(deleteServiceSQL)) {
	            psService.setInt(1, serviceId);
	            int serviceRowsDeleted = psService.executeUpdate();

	            // Commit the transaction if everything succeeded
	            conn.commit();

	            return serviceRowsDeleted > 0;
	        }
	    } catch (SQLException e) {
	        // If anything goes wrong, roll back the transaction
	        if (conn != null) {
	            try {
	                conn.rollback();
	            } catch (SQLException rollbackEx) {
	                rollbackEx.printStackTrace();
	            }
	        }
	        throw e; // Re-throw the original exception
	    } finally {
	        // Restore default auto-commit behavior and close connection
	        if (conn != null) {
	            try {
	                conn.setAutoCommit(true);
	                conn.close();
	            } catch (SQLException closeEx) {
	                closeEx.printStackTrace();
	            }
	        }
	    }
	}

	public List<Service> getServicesByCategory(int categoryId) throws SQLException, ClassNotFoundException {
		List<Service> services = new ArrayList<>();
		String selectSQL = "SELECT * FROM service WHERE category_id = ?";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(selectSQL)) {

			ps.setInt(1, categoryId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					services.add(new Service(rs.getInt("service_id"), rs.getInt("category_id"), rs.getInt("Status_id"),
							rs.getString("service_name"), rs.getDouble("price"), rs.getInt("duration_in_hour"),
							rs.getString("service_description")));
				}
			}
		}
		return services;
	}

	public Service getServiceById(int serviceId) throws SQLException, ClassNotFoundException {
		String selectSQL = "SELECT * FROM service WHERE service_id = ?";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(selectSQL)) {

			ps.setInt(1, serviceId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Service(rs.getInt("service_id"), rs.getInt("category_id"), rs.getInt("status_id"),
							rs.getString("service_name"), rs.getDouble("price"), rs.getInt("duration_in_hour"),
							rs.getString("service_description"));
				}
			}
		}
		return null;
	}
}
