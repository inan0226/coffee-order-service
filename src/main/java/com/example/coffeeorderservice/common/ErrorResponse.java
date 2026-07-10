package com.example.coffeeorderservice.common;

/**
 * 실패한 API 요청에 공통으로 사용하는 JSON 응답 형식입니다.
 *
 * @param code 오류를 식별하는 문자열
 * @param message 사용자가 확인할 수 있는 오류 설명
 */
public record ErrorResponse(String code, String message) {

	/**
	 * 내부 오류 코드를 API 응답용 데이터로 바꿉니다.
	 *
	 * @param errorCode 변환할 업무 오류 코드
	 * @return 오류 코드와 메시지가 담긴 응답 객체
	 */
	public static ErrorResponse from(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.message());
	}
}
