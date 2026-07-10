package com.example.coffeeorderservice.menu;

public record MenuResponse(long menuId, String name, long price) {

	public static MenuResponse from(CoffeeMenu menu) {
		return new MenuResponse(menu.id(), menu.name(), menu.price());
	}
}
