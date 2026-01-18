package com.herethere.withus.couple.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커플 연결 전 상대방 확인 응답 (미리보기)")
public record CoupleJoinPreviewResponse(
	@Schema(description = "초대장을 보낸 사람의 이름 (코드 생성자)", example = "김철수")
	String senderName,

	@Schema(description = "초대장을 받은 사람의 이름 (현재 로그인한 유저)", example = "이영희")
	String receiverName,

	@Schema(description = "입력한 8자리 초대 코드", example = "12345678")
	String inviteCode
) {
}
