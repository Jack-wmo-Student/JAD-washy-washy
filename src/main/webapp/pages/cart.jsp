<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List,MODEL.CLASS.cartItem"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>JAD-Washy-Washy - Cart</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/navbar.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/cart.css">
<link rel="icon"
	href="<%=request.getContextPath()%>/assets/icons/favicon.ico"
	type="image/x-icon">
</head>
<body>
	<!-- Include the Navbar -->
	<div>
		<%@ include file="/component/navbar.jsp"%>
	</div>

	<!-- Cart Container -->
	<div class="cart-container">
		<h1 class="cart-title">Your Cart</h1>
		<%
		// Retrieve the cart items from the session
		if (!sessionUtils.isLoggedIn(request, "isLoggedIn") || session == null) {
			// Handle invalid login
			request.setAttribute("error", "You must log in first.");
			request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
			return;
		}
		@SuppressWarnings("unchecked")
		List<cartItem> carts = (List<cartItem>) session.getAttribute("cart-item-list");

		if (carts != null && !carts.isEmpty()) {
			double totalPrice = 0.0;
			for (cartItem item : carts) {
				String itemName = (item.getService().getName() != null) ? item.getService().getName().trim()
				: "Unknown Service";
				String itemDescription = (item.getService().getDescription() != null)
				? item.getService().getDescription().trim()
				: "No description available.";
				double itemPrice = item.getService().getPrice();
				totalPrice += itemPrice;
		%>
		<div class="cart-item">
			<h2 class="item-title"><%=itemName%></h2>
			<p class="item-description"><%=itemDescription%></p>
			<p class="item-price">
				Price: $<%=itemPrice%></p>
			<p class="item-date">
				Booked Date:
				<%=item.getBookedDate()%></p>
			<p class="item-timeslot">
				Time Slot:
				<%=item.getTimeslot().getTimeRange()%></p>
			<form action="<%=request.getContextPath()%>/removeFromCart"
				method="post">
				<!-- not done -->
				<input type="hidden" name="itemId"
					value="<%=item.getService().getId()%>" />
				<button type="submit" class="remove-button">Remove</button>
			</form>
		</div>
		<hr>
		<%
		}
		%>
		<div class="cart-summary">
			<form action="<%=request.getContextPath()%>/pages/bookingPage.jsp"
				method="get">
				<button type="submit" class="add-more-button">Add More
					Items</button>
			</form>
			<h3>
				Total Price: $<%=totalPrice%></h3>
			<form action="<%=request.getContextPath()%>/cartHandler"
				method="post">
				<button type="submit" class="checkout-button">Proceed to
					Checkout</button>
			</form>
		</div>
		<%
		} else {
		%>
		<p>Your cart is empty. Add some services to get started!</p>
		<form action="<%=request.getContextPath()%>/pages/bookingPage.jsp"
			method="get">
			<button type="submit" class="add-more-button">Add More Items</button>
		</form>
		<%
		}
		%>
	</div>
</body>
</html>
