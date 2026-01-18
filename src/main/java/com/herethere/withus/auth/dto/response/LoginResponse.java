package com.herethere.withus.auth.dto.response;

import com.herethere.withus.couple.domain.OnboardingStatus;

public record LoginResponse(String jwt, OnboardingStatus onboardingStatus) {
}
