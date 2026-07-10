package com.example.coffeeorderservice.menu;

/**
 * 메뉴 목록 조회 API가 반환하는 JSON 한 건입니다.
 *
 * @param menuId 메뉴 식별값
 * @param name 메뉴 이름
 * @param price 메뉴 가격
 */
public record MenuResponse(long menuId, String name, long price) {

	/**
	 * 내부 메뉴 데이터를 외부 API 응답 형태로 바꿉니다.
	 *
	 * @param menu 변환할 메뉴
	 * @return 메뉴 조회 응답
	 */
	public static MenuResponse from(CoffeeMenu menu) {
		return new MenuResponse(menu.id(), menu.name(), menu.price());
	}
}
