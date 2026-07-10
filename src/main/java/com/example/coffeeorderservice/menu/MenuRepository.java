package com.example.coffeeorderservice.menu;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 메뉴 데이터를 조회하는 저장소의 약속입니다.
 *
 * <p>현재는 메모리 저장소를 사용하지만, 이 인터페이스를 유지하면 나중에 DB 저장소로
 * 바꾸더라도 Service 코드를 그대로 사용할 수 있습니다.</p>
 */
public interface MenuRepository extends JpaRepository<CoffeeMenu, Long> {
}
