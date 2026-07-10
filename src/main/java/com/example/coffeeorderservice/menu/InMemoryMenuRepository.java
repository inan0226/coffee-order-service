package com.example.coffeeorderservice.menu;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryMenuRepository implements MenuRepository {

	private final List<CoffeeMenu> menus = List.of(
			new CoffeeMenu(1L, "Americano", 4_500L),
			new CoffeeMenu(2L, "Cafe Latte", 5_000L),
			new CoffeeMenu(3L, "Cappuccino", 5_500L),
			new CoffeeMenu(4L, "Cafe Mocha", 6_000L)
	);

	@Override
	public List<CoffeeMenu> findAll() {
		return menus;
	}

	@Override
	public Optional<CoffeeMenu> findById(long menuId) {
		return menus.stream()
				.filter(menu -> menu.id() == menuId)
				.findFirst();
	}
}
