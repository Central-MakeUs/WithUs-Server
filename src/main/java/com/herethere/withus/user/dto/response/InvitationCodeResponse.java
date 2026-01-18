package com.herethere.withus.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커플 초대 코드 생성 응답")
public record InvitationCodeResponse(
	@Schema(
		description = "상대방에게 전달할 8자리 숫자 초대 코드",
		example = "12345678",
		pattern = "^[0-9]{8}$"
	)
	String invitationCode
) {
}
