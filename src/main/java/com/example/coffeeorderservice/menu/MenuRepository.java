package com.example.coffeeorderservice.menu;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 메뉴 데이터를 조회하는 저장소의 약속입니다.
 *
 * <p>Spring Data JPA가 공유 데이터베이스에서 메뉴를 조회합니다.</p>
 */
public interface MenuRepository extends JpaRepository<CoffeeMenu, Long> {
}
