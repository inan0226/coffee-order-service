package com.example.coffeeorderservice.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 커피 주문 API가 받는 JSON 요청입니다.
 *
 * @param userId 주문하고 결제할 사용자 식별값. 1 이상이어야 합니다.
 * @param menuId 주문할 메뉴 식별값. 1 이상이어야 합니다.
 */
public record OrderRequest(
		@NotNull @Positive Long userId,
		@NotNull @Positive Long menuId
) {
}
