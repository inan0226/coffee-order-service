package com.example.coffeeorderservice.common;

import org.springframework.http.HttpStatus;

/**
 * API에서 클라이언트에게 알려 줄 업무 오류의 종류를 모아 둔 열거형입니다.
 *
 * <p>오류마다 HTTP 상태 코드와 사용자에게 보여 줄 한글 메시지를 함께 관리하여,
 * 어느 API에서 오류가 나도 응답 형식을 일관되게 유지합니다.</p>
 */
public enum ErrorCode {

	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값을 확인해주세요."),
	INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액은 1P 이상이어야 합니다."),
	POINT_BALANCE_OVERFLOW(HttpStatus.BAD_REQUEST, "포인트 잔액이 허용 범위를 초과했습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 포인트 정보를 찾을 수 없습니다."),
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다."),
	INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "포인트 잔액이 부족합니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * 오류에 맞는 HTTP 상태 코드입니다.
	 *
	 * @return 클라이언트 응답에 사용할 상태 코드
	 */
	public HttpStatus status() {
		return status;
	}

	/**
	 * 오류를 설명하는 사용자용 메시지입니다.
	 *
	 * @return 오류 메시지
	 */
	public String message() {
		return message;
	}
}
