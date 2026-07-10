package com.example.coffeeorderservice.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값을 확인해주세요."),
	INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액은 1P 이상이어야 합니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 포인트 정보를 찾을 수 없습니다."),
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다."),
	INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "포인트 잔액이 부족합니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus status() {
		return status;
	}

	public String message() {
		return message;
	}
}
