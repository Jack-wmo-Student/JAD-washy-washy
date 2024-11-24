package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

public class sessionUtils {

    // Check if the user is logged in
    public static boolean isLoggedIn(HttpServletRequest request,String cookieName) {
            Cookie[] cookies = request.getCookies(); // Get all cookies
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
                    	return cookie!=null && Boolean.parseBoolean(cookie.getValue()); // Return the cookie's value if found
                    }
                }
            }
            return false; // Return null if the cookie is not found
        }
    // Check if the user is an admin
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Do not create a new session
        if (session != null) {
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            return isAdmin != null && isAdmin; // Ensure the value is true
        }
        return false;
    }
}