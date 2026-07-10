package com.example.coffeeorderservice.order;

import java.time.Instant;
import java.util.List;

public interface OrderRepository {

	CoffeeOrder save(long userId, long menuId, long paidAmount, Instant orderedAt);

	List<CoffeeOrder> findAll();

	List<CoffeeOrder> findByOrderedAtBetween(Instant startInclusive, Instant endInclusive);
}
