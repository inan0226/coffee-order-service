package com.example.coffeeorderservice.point;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * 사용자 포인트 잔액을 서버 메모리에 보관하는 저장소입니다.
 *
 * <p>동시에 여러 요청이 들어와도 Map 자체는 안전하게 사용할 수 있도록 ConcurrentHashMap을 사용합니다.
 * 서버를 재시작하면 이 데이터는 유지되지 않습니다.</p>
 */
@Repository
public class InMemoryPointRepository implements PointRepository {

	private final Map<Long, Long> balances = new ConcurrentHashMap<>();

	/**
	 * 메모리에서 사용자 잔액을 조회합니다.
	 *
	 * @param userId 사용자 식별값
	 * @return 저장된 잔액 또는 빈 Optional
	 */
	@Override
	public Optional<Long> findBalanceByUserId(long userId) {
		return Optional.ofNullable(balances.get(userId));
	}

	/**
	 * 메모리에 사용자 잔액을 저장하거나 기존 값을 갱신합니다.
	 *
	 * @param userId 사용자 식별값
	 * @param balance 저장할 잔액
	 */
	@Override
	public void save(long userId, long balance) {
		balances.put(userId, balance);
	}
}
