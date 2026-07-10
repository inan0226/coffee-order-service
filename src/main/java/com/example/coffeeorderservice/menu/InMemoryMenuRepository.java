package com.example.coffeeorderservice.menu;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 과제 실행을 위해 기본 메뉴를 메모리에 보관하는 저장소입니다.
 *
 * <p>서버가 재시작되면 메모리 데이터도 다시 초기 메뉴 목록으로 돌아갑니다.
 * 실제 서비스에서는 이 부분을 DB 조회 코드로 교체할 수 있습니다.</p>
 */
@Repository
public class InMemoryMenuRepository implements MenuRepository {

	private final List<CoffeeMenu> menus = List.of(
			new CoffeeMenu(1L, "Americano", 4_500L),
			new CoffeeMenu(2L, "Cafe Latte", 5_000L),
			new CoffeeMenu(3L, "Cappuccino", 5_500L),
			new CoffeeMenu(4L, "Cafe Mocha", 6_000L)
	);

	/**
	 * 미리 정의한 메뉴를 등록된 순서대로 반환합니다.
	 *
	 * @return 초기 메뉴 목록
	 */
	@Override
	public List<CoffeeMenu> findAll() {
		return menus;
	}

	/**
	 * 초기 메뉴 목록에서 같은 ID의 메뉴를 찾습니다.
	 *
	 * @param menuId 찾을 메뉴 ID
	 * @return 찾은 메뉴 또는 빈 Optional
	 */
	@Override
	public Optional<CoffeeMenu> findById(long menuId) {
		return menus.stream()
				.filter(menu -> menu.id() == menuId)
				.findFirst();
	}
}
