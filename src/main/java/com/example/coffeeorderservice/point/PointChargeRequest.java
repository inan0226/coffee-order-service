package com.example.coffeeorderservice.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 포인트 충전 API가 받는 JSON 요청입니다.
 *
 * <p>{@link Positive} 검증을 통해 사용자 ID와 충전 금액이 모두 1 이상인지 Controller 진입 시 확인합니다.</p>
 *
 * @param userId 포인트를 충전할 사용자 식별값
 * @param amount 충전할 금액. 1원은 1포인트로 계산됩니다.
 */
public record PointChargeRequest(
		@NotNull @Positive Long userId,
		@NotNull @Positive Long amount
) {
}
