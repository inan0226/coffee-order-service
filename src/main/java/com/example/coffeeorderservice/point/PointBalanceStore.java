package com.example.coffeeorderservice.point;

import java.util.Optional;

/**
 * 사용자 포인트 잔액을 원자적으로 바꾸는 저장소의 약속입니다.
 *
 * <p>구현체는 공유 데이터베이스에서 조건부 UPDATE를 실행하므로,
 * 여러 서버 인스턴스가 동시에 요청을 처리해도 잔액 계산이 서로 덮어써지지 않습니다.</p>
 */
public interface PointBalanceStore {

	/**
	 * 이미 존재하는 사용자의 잔액을 원자적으로 증가시킵니다.
	 *
	 * @param userId 사용자 식별값
	 * @param amount 추가할 포인트
	 * @return 변경된 행 수
	 */
	int increaseExisting(long userId, long amount);

	/**
	 * 포인트 정보가 없는 사용자의 첫 잔액을 생성합니다.
	 *
	 * @param userId 사용자 식별값
	 * @param balance 최초 포인트 잔액
	 */
	void create(long userId, long balance);

	/**
	 * 잔액이 충분할 때만 포인트를 원자적으로 차감합니다.
	 *
	 * @param userId 사용자 식별값
	 * @param amount 차감할 포인트
	 * @return 차감에 성공한 행 수
	 */
	int deductIfEnough(long userId, long amount);

	/**
	 * 사용자의 현재 잔액을 조회합니다.
	 *
	 * @param userId 사용자 식별값
	 * @return 잔액 또는 빈 Optional
	 */
	Optional<Long> findBalance(long userId);

	/**
	 * 사용자의 포인트 행 존재 여부를 확인합니다.
	 *
	 * @param userId 사용자 식별값
	 * @return 포인트 행이 있으면 true
	 */
	boolean exists(long userId);
}
