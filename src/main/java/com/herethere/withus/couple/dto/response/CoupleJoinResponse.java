package com.herethere.withus.couple.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleJoinResponse(
	@Schema(description = "생성된 커플의 ID", example = "101")
	Long coupleId) {
}
