package com.example.coffeeorderservice.order;

import java.time.Instant;

public record CoffeeOrder(
		long id,
		long userId,
		long menuId,
		long paidAmount,
		Instant orderedAt
) {
}
