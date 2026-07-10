package com.example.coffeeorderservice.menu;

/**
 * 인기 메뉴 조회 API가 반환하는 JSON 한 건입니다.
 *
 * @param menuId 메뉴 식별값
 * @param name 메뉴 이름
 * @param price 메뉴 가격
 * @param orderCount 최근 7일 동안 성공한 주문 횟수
 */
public record PopularMenuResponse(long menuId, String name, long price, long orderCount) {

	/**
	 * 메뉴 정보와 집계된 주문 횟수를 하나의 응답으로 묶습니다.
	 *
	 * @param menu 메뉴 정보
	 * @param orderCount 해당 메뉴의 주문 횟수
	 * @return 인기 메뉴 응답
	 */
	public static PopularMenuResponse of(CoffeeMenu menu, long orderCount) {
		return new PopularMenuResponse(menu.id(), menu.name(), menu.price(), orderCount);
	}
}
