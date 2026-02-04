package com.herethere.withus.auth.externalapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class FeignConfig {
	@Bean
	Logger.Level feignLoggerLevel() {
		// FULL: Request/Response의 Headers, Body, Metadata를 모두 남김
		return Logger.Level.FULL;
	}

	@Bean
	Encoder feignFormEncoder() {
		return new SpringFormEncoder();
	}
}
