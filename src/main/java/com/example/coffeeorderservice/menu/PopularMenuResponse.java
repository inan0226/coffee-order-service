package com.example.coffeeorderservice.menu;

public record PopularMenuResponse(long menuId, String name, long price, long orderCount) {

	public static PopularMenuResponse of(CoffeeMenu menu, long orderCount) {
		return new PopularMenuResponse(menu.id(), menu.name(), menu.price(), orderCount);
	}
}
