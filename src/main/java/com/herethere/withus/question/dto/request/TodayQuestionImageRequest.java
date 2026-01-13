package com.herethere.withus.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodayQuestionImageRequest(@NotNull Long coupleQuestionId, @NotBlank String imageKey) {
}
