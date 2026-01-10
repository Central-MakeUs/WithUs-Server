package com.herethere.withus.auth.externalapi;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
	@Bean
	Logger.Level feignLoggerLevel() {
		// FULL: Request/Response의 Headers, Body, Metadata를 모두 남김
		return Logger.Level.FULL;
	}
}
