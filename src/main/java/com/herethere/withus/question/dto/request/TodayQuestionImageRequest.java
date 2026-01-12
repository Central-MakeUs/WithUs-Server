package com.herethere.withus.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TodayQuestionImageRequest(@NotBlank String imageKey) {
}
