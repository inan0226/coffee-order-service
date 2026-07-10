package com.example.coffeeorderservice.common;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.errorCode();
		return ResponseEntity.status(errorCode.status())
				.body(ErrorResponse.from(errorCode));
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
		return ResponseEntity.badRequest()
				.body(ErrorResponse.from(ErrorCode.INVALID_REQUEST));
	}
}
