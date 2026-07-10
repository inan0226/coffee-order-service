package com.example.coffeeorderservice.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 현재 시간을 한곳에서 제공하는 설정입니다.
 *
 * <p>서비스가 {@link java.time.Instant#now()}를 직접 호출하지 않고 Clock을 받으면,
 * 테스트에서 원하는 시각으로 바꿔 최근 7일 같은 시간 조건을 쉽게 검증할 수 있습니다.</p>
 */
@Configuration
public class ClockConfig {

	/**
	 * 운영 환경에서 사용할 UTC 기준 시계를 만듭니다.
	 *
	 * @return 현재 UTC 시간을 제공하는 Clock
	 */
	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}
