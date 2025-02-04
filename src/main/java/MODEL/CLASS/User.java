package MODEL.CLASS;

public class User {
	private int user_id;
	private String user_name;
	private int is_admin;
	private int is_blocked;

	public User(int user_id, String user_name, int is_admin, int is_blocked) {
		this.user_id = user_id;
		this.user_name = user_name;
		this.is_admin = is_admin;
		this.is_blocked = is_blocked;
	}

	public User() {
//		this.user_id = 1;
//		this.user_name = "John Doe";
//		this.is_admin = false;
//		this.is_blocked = false;
	}

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return user_name;
	}

	public void setUsername(String user_name) {
		this.user_name = user_name;
	}

	public boolean isIsAdmin() {
		return is_admin == 1;
	}

	public void setIsAdmin(int is_admin) {
		this.is_admin = is_admin;
	}

	public boolean isIsBlocked() {
		return is_blocked == 1;
	}

	public void setIsBlocked(int is_blocked) {
		this.is_blocked = is_blocked;
	}
}