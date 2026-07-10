package com.example.coffeeorderservice.order;

public record OrderResponse(
		long orderId,
		long userId,
		long menuId,
		long paidAmount,
		long remainingBalance
) {
}
