package model;

public class user {
	private int user_id;
    private String username;
    private boolean isAdmin;

    public user(int user_id, String username, boolean isAdmin) {
    	this.user_id = user_id;
        this.username = username;
        this.isAdmin = isAdmin;
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
}
