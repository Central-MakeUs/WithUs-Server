package com.herethere.withus.keyword.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodayKeywordImageRequest(@NotNull Long coupleKeywordId, @NotBlank String imageKey) {
}
