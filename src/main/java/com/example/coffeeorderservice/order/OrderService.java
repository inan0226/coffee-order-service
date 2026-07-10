package com.example.coffeeorderservice.order;

import com.example.coffeeorderservice.menu.CoffeeMenu;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final MenuService menuService;
	private final PointService pointService;
	private final OrderRepository orderRepository;
	private final OrderEventClient orderEventClient;
	private final Clock clock;

	public OrderService(
			MenuService menuService,
			PointService pointService,
			OrderRepository orderRepository,
			OrderEventClient orderEventClient,
			Clock clock
	) {
		this.menuService = menuService;
		this.pointService = pointService;
		this.orderRepository = orderRepository;
		this.orderEventClient = orderEventClient;
		this.clock = clock;
	}

	public synchronized OrderResponse order(long userId, long menuId) {
		CoffeeMenu menu = menuService.getMenu(menuId);
		long remainingBalance = pointService.deduct(userId, menu.price());
		CoffeeOrder order = orderRepository.save(userId, menu.id(), menu.price(), clock.instant());
		orderEventClient.send(new OrderEvent(userId, menu.id(), menu.price()));

		return new OrderResponse(order.id(), userId, menu.id(), menu.price(), remainingBalance);
	}
}
