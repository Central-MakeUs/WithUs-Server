package com.herethere.withus.fourcut.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FourCutUploadRequest(
	@Schema(description = "/api/images/presinged-url의 응답에서 받은 imageKey", example = "users/1/four-cut/123123123.jpg")
	@NotBlank String imageKey) {
}
