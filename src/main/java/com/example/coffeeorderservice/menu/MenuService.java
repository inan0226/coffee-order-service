package com.example.coffeeorderservice.menu;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
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
				.orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
	}
}
