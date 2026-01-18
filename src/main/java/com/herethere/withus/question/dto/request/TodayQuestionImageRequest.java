package com.herethere.withus.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TodayQuestionImageRequest(
	@Schema(description = "/api/images/presinged-url의 응답에서 받은 imageKey", example = "users/1/memory/123123123.jpg")
	@NotBlank String imageKey) {
}
