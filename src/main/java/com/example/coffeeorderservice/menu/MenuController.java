package com.example.coffeeorderservice.menu;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

	private final MenuService menuService;
	private final PopularMenuService popularMenuService;

	public MenuController(MenuService menuService, PopularMenuService popularMenuService) {
		this.menuService = menuService;
		this.popularMenuService = popularMenuService;
	}

	@GetMapping
	public List<MenuResponse> getMenus() {
		return menuService.getMenus();
	}

	@GetMapping("/popular")
	public List<PopularMenuResponse> getPopularMenus() {
		return popularMenuService.getPopularMenus();
	}
}
