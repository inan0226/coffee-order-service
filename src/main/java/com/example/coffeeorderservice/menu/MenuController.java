package com.example.coffeeorderservice.menu;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메뉴 관련 HTTP 요청을 받는 Controller입니다.
 *
 * <p>{@code /api/menus}에서는 전체 메뉴를, {@code /api/menus/popular}에서는
 * 최근 7일 인기 메뉴를 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/menus")
public class MenuController {

	private final MenuService menuService;
	private final PopularMenuService popularMenuService;

	public MenuController(MenuService menuService, PopularMenuService popularMenuService) {
		this.menuService = menuService;
		this.popularMenuService = popularMenuService;
	}

	/**
	 * 판매 중인 전체 메뉴를 조회합니다.
	 *
	 * @return 메뉴 ID, 이름, 가격 목록
	 */
	@GetMapping
	public List<MenuResponse> getMenus() {
		return menuService.getMenus();
	}

	/**
	 * 최근 7일 동안 주문이 많은 메뉴 3개를 조회합니다.
	 *
	 * @return 주문 횟수가 포함된 인기 메뉴 목록
	 */
	@GetMapping("/popular")
	public List<PopularMenuResponse> getPopularMenus() {
		return popularMenuService.getPopularMenus();
	}
}
