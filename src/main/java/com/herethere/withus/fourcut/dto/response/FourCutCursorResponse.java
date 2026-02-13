package com.herethere.withus.fourcut.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "네컷 사진 커서 기반 페이지네이션 응답")
public record FourCutCursorResponse(
	@Schema(description = "네컷 사진 목록")
	List<FourCutInfo> fourCuts,

	@Schema(
		description = "다음 페이지 조회를 위한 커서 값 (없으면 null)",
		example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTIzVDAxOjExOjUyIiwiaWQiOjExOX0="
	)
	String nextCursor,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext

) {	@Schema(description = "네컷 사진 정보")
	public record FourCutInfo(

		@Schema(description = "네컷 사진 ID", example = "123")
		Long fourCutId,

		@Schema(description = "네컷 사진 썸네일 이미지 URL", example = "https://cdn.withus.com/fourcuts/123.png")
		String thumbnailUrl,

		@Schema(description = "생성 일시 (Asia/Seoul)", example = "2026-01-23T01:11:52")
		LocalDateTime createdAt
	) {
	}
}
