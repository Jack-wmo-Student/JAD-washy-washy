package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import MODEL.CLASS.CartItem;
import MODEL.DAO.CartDAO;
import MODEL.CLASS.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class cartHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public cartHandler() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("cart-item-list") == null) {
			response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
			return;
		}

		@SuppressWarnings("unchecked")
		List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart-item-list");
		if (cartItems == null) {
			response.sendRedirect(
					request.getContextPath() + "/pages/bookingPage.jsp?error=Invalid session. Please log in again.");
			return;
		}

		User user = (User) session.getAttribute("currentUser");
		if (user == null) {
			response.sendRedirect(
					request.getContextPath() + "/pages/login.jsp?error=Invalid session. Please log in again.");
			return;
		}

		int userId = user.getUserId();
		List<Integer> bookingIdLists = new ArrayList<>();

		try {
			CartDAO cartModel = new CartDAO();
			bookingIdLists = cartModel.processCartItems(userId, cartItems);
			session.setAttribute("bookingIdLists", bookingIdLists);
			session.removeAttribute("cart-item-list"); // Clear cart after booking
			response.sendRedirect(request.getContextPath() + "/feedbackLogic");
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "An error occurred while processing your booking. Please try again.");
			request.getRequestDispatcher("/pages/cart.jsp").forward(request, response);
		}
	}
}
