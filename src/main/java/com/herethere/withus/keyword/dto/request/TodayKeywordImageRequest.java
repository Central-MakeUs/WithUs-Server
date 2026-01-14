package com.herethere.withus.keyword.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TodayKeywordImageRequest(@NotBlank String imageKey) {
}
