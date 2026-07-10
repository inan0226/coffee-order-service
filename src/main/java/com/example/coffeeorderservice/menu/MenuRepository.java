package com.example.coffeeorderservice.menu;

import java.util.List;
import java.util.Optional;

/**
 * 메뉴 데이터를 조회하는 저장소의 약속입니다.
 *
 * <p>현재는 메모리 저장소를 사용하지만, 이 인터페이스를 유지하면 나중에 DB 저장소로
 * 바꾸더라도 Service 코드를 그대로 사용할 수 있습니다.</p>
 */
public interface MenuRepository {

	/**
	 * 판매 중인 모든 메뉴를 반환합니다.
	 *
	 * @return 메뉴 목록
	 */
	List<CoffeeMenu> findAll();

	/**
	 * 메뉴 ID로 메뉴를 찾습니다.
	 *
	 * @param menuId 찾을 메뉴 ID
	 * @return 메뉴가 있으면 해당 메뉴, 없으면 빈 Optional
	 */
	Optional<CoffeeMenu> findById(long menuId);
}
