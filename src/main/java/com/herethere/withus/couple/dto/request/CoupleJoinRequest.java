package com.herethere.withus.couple.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CoupleJoinRequest(
	@Schema(
		description = "상대방으로부터 받은 8자리 숫자 초대 코드",
		example = "12345678",
		pattern = "^[0-9]{8}$"
	)
	@NotBlank(message = "초대 코드는 필수입니다.")
	@Pattern(
		regexp = "^[0-9]{8}$",
		message = "초대 코드는 8자리 숫자여야 합니다."
	) String inviteCode) {
}
