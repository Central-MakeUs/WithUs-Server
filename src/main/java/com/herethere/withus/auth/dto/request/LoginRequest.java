package com.herethere.withus.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank String oauthToken,
	@NotBlank String fcmToken) {
}
