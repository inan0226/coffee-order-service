package com.example.coffeeorderservice.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 데이터베이스에 저장되는 커피 메뉴 한 가지입니다.
 *
 * <p>메뉴는 Flyway 마이그레이션으로 초기화되며 주문 과정에서는 가격을 읽는 용도로만 사용합니다.</p>
 *
 * @param id 메뉴를 구분하는 식별값
 * @param name 화면과 API에 보여 줄 메뉴 이름
 * @param price 메뉴의 가격이자 주문 시 차감할 포인트
 */
@Entity
@Table(name = "coffee_menus")
public class CoffeeMenu {

	@Id
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private long price;

	protected CoffeeMenu() {
	}

	public CoffeeMenu(long id, String name, long price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public long price() {
		return price;
	}
}
