package model;

public class user {
	private int user_id;
    private String username;
    private boolean isAdmin;
	private boolean isBlocked;

    public user(int user_id, String username, boolean isAdmin, boolean isBlocked) {
    	this.user_id = user_id;
        this.username = username;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }
    
    public user() {
    	this.user_id = 1;
        this.username = "John Doe";
        this.isAdmin = false;
        this.isBlocked = false;
    }
    
    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    
    public int getUser_id() {
        return user_id;
    }
    
    public boolean isBlocked() {
        return isBlocked;
    }

	public void setUserId(int id) {
		this.user_id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
}