package com.example.coffeeorderservice.menu;

/**
 * DB 인기 메뉴 집계 쿼리가 반환하는 메뉴 ID와 주문 횟수입니다.
 *
 * @param menuId 주문을 집계한 메뉴 ID
 * @param orderCount 해당 메뉴의 성공 주문 횟수
 */
public record MenuOrderCount(long menuId, long orderCount) {
}
