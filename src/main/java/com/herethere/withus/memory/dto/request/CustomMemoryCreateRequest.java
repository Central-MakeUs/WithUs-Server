package com.herethere.withus.memory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomMemoryCreateRequest(
	@NotBlank(message = "이미지는 필수입니다.")
	@Size(max = 255, message = "이미지 키가 너무 깁니다.")
	String imageKey,

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 20, message = "제목이 너무 깁니다.")
	String title
) {
}
