package com.herethere.withus.memory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemoryCreateRequest(
	@NotBlank(message = "이미지는 필수입니다.")
	@Size(max = 255, message = "이미지 키가 너무 깁니다.")
	String imageKey) {
}
