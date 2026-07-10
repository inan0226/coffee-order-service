package com.example.coffeeorderservice.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PointServiceTest {

	private final PointBalanceStore pointBalanceStore = mock(PointBalanceStore.class);
	private final PointService pointService = new PointService(pointBalanceStore);

	@Test
	void 신규_사용자는_요청한_금액만큼_포인트를_충전한다() {
		when(pointBalanceStore.increaseExisting(1L, 10_000L)).thenReturn(0);
		when(pointBalanceStore.findBalance(1L)).thenReturn(Optional.of(10_000L));

		PointBalanceResponse response = pointService.charge(1L, 10_000L);

		assertThat(response).isEqualTo(new PointBalanceResponse(1L, 10_000L));
	}

	@Test
	void 기존_사용자의_충전_금액은_누적된다() {
		when(pointBalanceStore.increaseExisting(1L, 10_000L)).thenReturn(1);
		when(pointBalanceStore.findBalance(1L)).thenReturn(Optional.of(10_000L));
		pointService.charge(1L, 10_000L);

		when(pointBalanceStore.increaseExisting(1L, 5_000L)).thenReturn(1);
		when(pointBalanceStore.findBalance(1L)).thenReturn(Optional.of(15_000L));
		PointBalanceResponse response = pointService.charge(1L, 5_000L);

		assertThat(response.balance()).isEqualTo(15_000L);
	}

	@Test
	void 충전_금액이_0이하면_예외가_발생한다() {
		assertThatThrownBy(() -> pointService.charge(1L, 0L))
				.isInstanceOf(BusinessException.class)
				.extracting(exception -> ((BusinessException) exception).errorCode())
				.isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);

		verifyNoInteractions(pointBalanceStore);
	}
}
