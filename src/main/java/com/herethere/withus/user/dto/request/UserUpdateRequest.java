package com.herethere.withus.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(@NotBlank(message = "닉네임은 필수 항목입니다.") String nickname,
								String imageObjectKey) {
}
