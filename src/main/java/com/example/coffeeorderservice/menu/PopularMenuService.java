package com.example.coffeeorderservice.menu;

import com.example.coffeeorderservice.order.CoffeeOrderJpaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 최근 7일 주문 기록을 메뉴별로 집계해 인기 메뉴를 계산하는 Service입니다.
 *
 * <p>주문 횟수가 많은 순서로 정렬하고, 횟수가 같으면 메뉴 ID가 작은 메뉴를 먼저 반환해
 * 언제 조회해도 같은 순서가 나오도록 합니다.</p>
 */
@Service
public class PopularMenuService {

	private final MenuRepository menuRepository;
	private final CoffeeOrderJpaRepository coffeeOrderJpaRepository;
	private final Clock clock;

	public PopularMenuService(
			MenuRepository menuRepository,
			CoffeeOrderJpaRepository coffeeOrderJpaRepository,
			Clock clock
	) {
		this.menuRepository = menuRepository;
		this.coffeeOrderJpaRepository = coffeeOrderJpaRepository;
		this.clock = clock;
	}

	/**
	 * 현재 시점부터 7일 전까지의 성공 주문을 집계해 상위 3개 메뉴를 반환합니다.
	 *
	 * @return 주문 횟수 내림차순, 메뉴 ID 오름차순으로 정렬된 인기 메뉴 최대 3개
	 */
	public List<PopularMenuResponse> getPopularMenus() {
		Instant now = clock.instant();
		Instant sevenDaysAgo = now.minus(7, ChronoUnit.DAYS);
		List<MenuOrderCount> menuOrderCounts = coffeeOrderJpaRepository.findPopularMenuCounts(
				sevenDaysAgo,
				now,
				PageRequest.of(0, 3)
		);
		if (menuOrderCounts.isEmpty()) {
			return List.of();
		}

		Map<Long, CoffeeMenu> menusById = menuRepository.findAllById(
				menuOrderCounts.stream().map(MenuOrderCount::menuId).toList()
		).stream().collect(Collectors.toMap(CoffeeMenu::id, Function.identity()));

		return menuOrderCounts.stream()
				.map(menuOrderCount -> PopularMenuResponse.of(
						findMenu(menusById, menuOrderCount.menuId()),
						menuOrderCount.orderCount()
				))
				.toList();
	}

	/**
	 * 일괄 조회한 메뉴에서 집계 결과에 해당하는 메뉴를 찾습니다.
	 *
	 * <p>주문 집계와 메뉴 조회 사이에 메뉴가 삭제된 경우에는 일관성이 깨진 상태이므로,
	 * 누락을 조용히 무시하지 않고 예외로 처리합니다.</p>
	 *
	 * @param menusById 메뉴 ID를 키로 하는 일괄 조회 결과
	 * @param menuId 주문 집계에 포함된 메뉴 식별값
	 * @return 주문 집계와 연결할 메뉴 정보
	 * @throws IllegalStateException 집계 대상 메뉴를 찾지 못한 경우
	 */
	private CoffeeMenu findMenu(Map<Long, CoffeeMenu> menusById, long menuId) {
		CoffeeMenu menu = menusById.get(menuId);
		if (menu == null) {
			throw new IllegalStateException("주문 메뉴를 찾을 수 없습니다.");
		}
		return menu;
	}
}
