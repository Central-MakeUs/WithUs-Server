package com.herethere.withus.archive.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "질문 목록 응답 (무한 스크롤 포함)")
public record ArchiveQuestionListResponse(
	@Schema(description = "질문 리스트")
	List<QuestionInfo> questionList,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext,

	@Schema(description = "다음 조회를 위한 커서 값 (없으면 null)", example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTIzVDAxOjExOjUyIiwiaWQiOjExOX0")
	String nextCursor
) {

	@Schema(description = "질문 정보")
	public record QuestionInfo(
		@Schema(description = "couple-question이 연결된 id 값입니다. 추후 상세 조회 때 사용합니다.", example = "12")
		Long coupleQuestionId,

		@Schema(description = "번호")
		Long questionNumber,

		@Schema(description = "질문 내용")
		String questionContent
	) {
	}
}
