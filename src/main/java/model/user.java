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

    public int getId() {
    	return user_id;
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
}