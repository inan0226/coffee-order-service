package com.example.coffeeorderservice.menu;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 메뉴 조회와 메뉴 존재 여부 확인을 담당하는 Service입니다.
 *
 * <p>Controller는 이 Service를 호출해 API 응답만 만들고,
 * 메뉴를 찾는 규칙은 이 클래스에 모아 둡니다.</p>
 */
@Service
public class MenuService {

	private final MenuRepository menuRepository;

	public MenuService(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	/**
	 * 모든 메뉴를 API 응답 형식으로 반환합니다.
	 *
	 * @return 메뉴 ID, 이름, 가격이 담긴 목록
	 */
	public List<MenuResponse> getMenus() {
		return menuRepository.findAllByOrderByIdAsc().stream()
				.map(MenuResponse::from)
				.toList();
	}

	/**
	 * 주문에 사용할 메뉴를 찾습니다.
	 *
	 * <p>없는 메뉴 ID라면 주문을 진행할 수 없으므로 {@code MENU_NOT_FOUND} 업무 예외를 발생시킵니다.</p>
	 *
	 * @param menuId 주문하려는 메뉴 ID
	 * @return 존재하는 메뉴
	 */
	public CoffeeMenu getMenu(long menuId) {
		return menuRepository.findById(menuId)
				.orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
	}
}
