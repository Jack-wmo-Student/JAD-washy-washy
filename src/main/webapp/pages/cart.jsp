<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.cart"%>
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
		<%@ include file="/component/navbar.jsp" %>
	</div>

	<!-- Cart Container -->
	<div class="cart-container">
		<h1 class="cart-title">Your Cart</h1>
		<%
		// Retrieve the cart items from the session
		@SuppressWarnings("unchecked")
		List<cart> carts = (List<cart>) session.getAttribute("cart");

		if (carts != null && !carts.isEmpty()) {
			double totalPrice = 0.0;
			for (cart item : carts) {
				String itemName = (item.getServiceName() != null) ? item.getServiceName().trim() : "Unknown Service";
				String itemDescription = (item.getDescription() != null) ? item.getDescription().trim()
						: "No description available.";
				double itemPrice = item.getPrice();
				int itemQuantity = item.getQuantity();
				totalPrice += itemPrice * itemQuantity;
		%>
		<div class="cart-item">
			<h2 class="item-title"><%=itemName%></h2>
			<p class="item-description"><%=itemDescription%></p>
			<p class="item-price">Price: $<%=itemPrice%></p>
			<p class="item-quantity">Quantity: <%=itemQuantity%></p>
			<p class="item-total">Total: $<%=itemPrice * itemQuantity%></p>
			<form action="<%=request.getContextPath()%>/updateCart" method="post">
				<input type="hidden" name="itemId" value="<%=item.getId()%>" />
				<label for="quantity-<%=item.getId()%>">Update Quantity:</label>
				<input type="number" id="quantity-<%=item.getId()%>" name="quantity" min="1" value="<%=itemQuantity%>" />
				<button type="submit" class="update-button">Update</button>
			</form>
			<form action="<%=request.getContextPath()%>/removeFromCart" method="post">
				<input type="hidden" name="itemId" value="<%=item.getId()%>" />
				<button type="submit" class="remove-button">Remove</button>
			</form>
		</div>
		<hr>
		<%
			}
		%>
		<div class="cart-summary">
			<h3>Total Price: $<%=totalPrice%></h3>
			<form action="<%=request.getContextPath()%>/checkout" method="post">
				<button type="submit" class="checkout-button">Proceed to Checkout</button>
			</form>
		</div>
		<%
		} else {
		%>
		<p>Your cart is empty. Add some services to get started!</p>
		<%
		}
		%>
	</div>
</body>
</html>
