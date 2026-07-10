package com.example.coffeeorderservice.point;

import java.util.Optional;

/**
 * 사용자별 포인트 잔액을 저장하고 조회하는 저장소의 약속입니다.
 */
public interface PointRepository {

	/**
	 * 사용자의 현재 포인트 잔액을 찾습니다.
	 *
	 * @param userId 사용자 식별값
	 * @return 포인트 정보가 있으면 잔액, 없으면 빈 Optional
	 */
	Optional<Long> findBalanceByUserId(long userId);

	/**
	 * 사용자의 포인트 잔액을 새 값으로 저장합니다.
	 *
	 * @param userId 사용자 식별값
	 * @param balance 저장할 포인트 잔액
	 */
	void save(long userId, long balance);
}
