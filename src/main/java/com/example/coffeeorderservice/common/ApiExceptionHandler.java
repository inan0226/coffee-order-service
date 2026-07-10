package com.example.coffeeorderservice.common;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller에서 발생한 예외를 공통 JSON 오류 응답으로 바꾸는 클래스입니다.
 *
 * <p>각 Controller가 try-catch를 반복하지 않아도 되므로, Controller는 요청을 받고
 * Service를 호출하는 역할에만 집중할 수 있습니다.</p>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * 업무 규칙 위반 예외를 오류 코드에 맞는 HTTP 응답으로 변환합니다.
	 *
	 * @param exception Service에서 발생한 업무 예외
	 * @return 오류 코드와 메시지가 담긴 응답
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.errorCode();
		return ResponseEntity.status(errorCode.status())
				.body(ErrorResponse.from(errorCode));
	}

	/**
	 * JSON 형식이 잘못되었거나 요청 값 검증에 실패했을 때 400 응답을 만듭니다.
	 *
	 * @param exception 요청을 읽거나 검증하는 과정에서 발생한 예외
	 * @return 잘못된 요청을 알리는 공통 오류 응답
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
		return ResponseEntity.badRequest()
				.body(ErrorResponse.from(ErrorCode.INVALID_REQUEST));
	}
}
