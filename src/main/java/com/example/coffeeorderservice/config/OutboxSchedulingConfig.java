package com.example.coffeeorderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 아웃박스 재시도 작업을 실행하도록 Spring 스케줄러를 활성화합니다.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "outbox.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxSchedulingConfig {
}
