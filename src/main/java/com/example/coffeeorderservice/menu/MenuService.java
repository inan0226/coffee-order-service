package com.example.coffeeorderservice.menu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

	private final MenuRepository menuRepository;

	public MenuService(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	public List<MenuResponse> getMenus() {
		return menuRepository.findAll().stream()
				.map(MenuResponse::from)
				.toList();
	}

	public CoffeeMenu getMenu(long menuId) {
		return menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
	}
}
