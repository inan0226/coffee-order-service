package com.example.coffeeorderservice.menu;

/**
 * 판매하는 커피 메뉴 한 가지를 표현하는 읽기 전용 데이터입니다.
 *
 * @param id 메뉴를 구분하는 식별값
 * @param name 화면과 API에 보여 줄 메뉴 이름
 * @param price 메뉴의 가격이자 주문 시 차감할 포인트
 */
public record CoffeeMenu(long id, String name, long price) {
}
