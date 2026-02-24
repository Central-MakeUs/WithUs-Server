package com.herethere.withus.notification.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 목록 커서 기반 페이지네이션 응답")
public record NotificationCursorResponse(

	@Schema(description = "알림 목록")
	List<NotificationInfo> notifications,

	@Schema(
		description = "다음 페이지 조회를 위한 커서 값 (없으면 null)"
	)
	String nextCursor,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext

) {

	@Schema(description = "알림 정보")
	public record NotificationInfo(

		@Schema(description = "알림 제목", example = "오늘의 랜덤 질문이 도착했어요")
		String title,

		@Schema(description = "알림 내용", example = "오늘의 질문에 답해볼까요?")
		String content,

		@Schema(description = "이동할 화면 경로 (없으면 null)", example = "/today_question")
		String push
	) {
	}
}
