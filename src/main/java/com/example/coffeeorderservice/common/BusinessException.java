package com.example.coffeeorderservice.common;

/**
 * 포인트 부족이나 존재하지 않는 메뉴처럼 업무 규칙을 지키지 못했을 때 발생시키는 예외입니다.
 *
 * <p>예외 안에 {@link ErrorCode}를 보관해 두었다가
 * {@link ApiExceptionHandler}가 HTTP 오류 응답으로 변환합니다.</p>
 */
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	/**
	 * 지정한 오류 코드로 업무 예외를 만듭니다.
	 *
	 * @param errorCode 발생한 업무 오류의 종류
	 */
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	/**
	 * 발생한 업무 오류의 종류를 반환합니다.
	 *
	 * @return 오류 코드
	 */
	public ErrorCode errorCode() {
		return errorCode;
	}
}
