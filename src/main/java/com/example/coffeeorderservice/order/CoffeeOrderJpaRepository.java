package com.example.coffeeorderservice.order;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 성공한 주문 Entity를 저장하고 조회하는 JPA Repository입니다.
 */
public interface CoffeeOrderJpaRepository extends JpaRepository<CoffeeOrderEntity, Long> {
}
