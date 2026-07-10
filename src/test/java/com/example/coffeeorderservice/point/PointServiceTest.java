package com.example.coffeeorderservice.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import org.junit.jupiter.api.Test;

class PointServiceTest {

	private final PointService pointService = new PointService(new InMemoryPointRepository());

	@Test
	void 신규_사용자는_요청한_금액만큼_포인트를_충전한다() {
		PointBalanceResponse response = pointService.charge(1L, 10_000L);

		assertThat(response).isEqualTo(new PointBalanceResponse(1L, 10_000L));
	}

	@Test
	void 기존_사용자의_충전_금액은_누적된다() {
		pointService.charge(1L, 10_000L);

		PointBalanceResponse response = pointService.charge(1L, 5_000L);

		assertThat(response.balance()).isEqualTo(15_000L);
	}

	@Test
	void 충전_금액이_0이하면_예외가_발생한다() {
		assertThatThrownBy(() -> pointService.charge(1L, 0L))
				.isInstanceOf(BusinessException.class)
				.extracting(exception -> ((BusinessException) exception).errorCode())
				.isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);
	}
}
