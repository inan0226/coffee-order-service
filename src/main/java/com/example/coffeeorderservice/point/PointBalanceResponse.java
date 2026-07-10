package com.example.coffeeorderservice.point;

/**
 * 포인트 충전 후 사용자에게 반환하는 현재 잔액 정보입니다.
 *
 * @param userId 포인트를 충전한 사용자 식별값
 * @param balance 충전까지 반영된 최종 포인트 잔액
 */
public record PointBalanceResponse(long userId, long balance) {
}
