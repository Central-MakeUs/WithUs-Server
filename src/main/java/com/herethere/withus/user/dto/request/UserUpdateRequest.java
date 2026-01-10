package com.herethere.withus.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(@NotBlank(message = "닉네임은 필수 항목입니다.")
								@Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
								String nickname,
								String imageObjectKey) {
}
