package com.herethere.withus.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank(message = "FCMToken이 존재하지 않습니다.") String fcmToken) {
}
