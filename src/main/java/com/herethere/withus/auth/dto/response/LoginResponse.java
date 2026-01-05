package com.herethere.withus.auth.dto.response;

public record LoginResponse(String jwt, Boolean isNewUser) {
}
