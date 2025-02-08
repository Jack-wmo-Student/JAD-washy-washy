package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import MODEL.CLASS.CartItem;
import MODEL.CLASS.User;
import MODEL.DAO.CartDAO;
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

		// Get the action type from the form
		String action = request.getParameter("action");

		if ("remove".equals(action)) {
			removeItemFromCart(request, response, session);
		} else if ("checkout".equals(action)) {
			processCheckout(request, response, session);
		} else {
			response.sendRedirect(request.getContextPath() + "/pages/cart.jsp?error=Invalid action.");
		}
	}

	private void removeItemFromCart(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		String itemIdStr = request.getParameter("itemId");
		if (itemIdStr == null) {
			response.sendRedirect(request.getContextPath() + "/pages/cart.jsp?error=Invalid item.");
			return;
		}

		int itemId;
		try {
			itemId = Integer.parseInt(itemIdStr);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/pages/cart.jsp?error=Invalid item ID.");
			return;
		}

		@SuppressWarnings("unchecked")
		List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart-item-list");

		// Remove item from cart
		cartItems.removeIf(item -> item.getService().getId() == itemId);

		// Update session cart list
		session.setAttribute("cart-item-list", cartItems);

		response.sendRedirect(request.getContextPath() + "/pages/cart.jsp");
	}

	private void processCheckout(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException, ServletException {
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
