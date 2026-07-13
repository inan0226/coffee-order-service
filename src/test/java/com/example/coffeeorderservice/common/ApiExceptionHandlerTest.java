package com.example.coffeeorderservice.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

	private final ApiExceptionHandler apiExceptionHandler = new ApiExceptionHandler();

	@Test
	void 처리되지_않은_예외는_구현_정보_없이_일반적인_500_응답으로_변환한다() {
		ResponseEntity<ErrorResponse> response = apiExceptionHandler.handleUnexpectedException(
				new IllegalStateException("internal database detail")
		);

		assertThat(response.getStatusCode().value()).isEqualTo(500);
		assertThat(response.getBody())
				.isEqualTo(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
