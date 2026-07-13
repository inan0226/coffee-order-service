package com.example.coffeeorderservice.order;

import com.example.coffeeorderservice.menu.MenuOrderCount;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 성공한 주문 Entity를 저장하고 조회하는 JPA Repository입니다.
 */
public interface CoffeeOrderJpaRepository extends JpaRepository<CoffeeOrderEntity, Long> {

	/**
	 * 최근 기간에 성공한 주문을 메뉴별로 집계합니다.
	 *
	 * @param startInclusive 포함할 시작 시각
	 * @param endInclusive 포함할 종료 시각
	 * @param pageable 최대 조회 개수
	 * @return 주문 횟수 내림차순, 메뉴 ID 오름차순으로 정렬된 집계 결과
	 */
	@Query("""
			select new com.example.coffeeorderservice.menu.MenuOrderCount(coffeeOrder.menuId, count(coffeeOrder.id))
			from CoffeeOrderEntity coffeeOrder
			where coffeeOrder.orderedAt >= :startInclusive and coffeeOrder.orderedAt <= :endInclusive
			group by coffeeOrder.menuId
			order by count(coffeeOrder.id) desc, coffeeOrder.menuId asc
			""")
	List<MenuOrderCount> findPopularMenuCounts(
			@Param("startInclusive") Instant startInclusive,
			@Param("endInclusive") Instant endInclusive,
			Pageable pageable
	);
}
