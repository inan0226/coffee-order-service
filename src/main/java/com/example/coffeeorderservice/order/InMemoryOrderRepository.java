package com.example.coffeeorderservice.order;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

	private final AtomicLong sequence = new AtomicLong();
	private final List<CoffeeOrder> orders = new CopyOnWriteArrayList<>();

	@Override
	public CoffeeOrder save(long userId, long menuId, long paidAmount, Instant orderedAt) {
		CoffeeOrder order = new CoffeeOrder(sequence.incrementAndGet(), userId, menuId, paidAmount, orderedAt);
		orders.add(order);
		return order;
	}

	@Override
	public List<CoffeeOrder> findAll() {
		return List.copyOf(orders);
	}

	@Override
	public List<CoffeeOrder> findByOrderedAtBetween(Instant startInclusive, Instant endInclusive) {
		return orders.stream()
				.filter(order -> !order.orderedAt().isBefore(startInclusive))
				.filter(order -> !order.orderedAt().isAfter(endInclusive))
				.toList();
	}
}
