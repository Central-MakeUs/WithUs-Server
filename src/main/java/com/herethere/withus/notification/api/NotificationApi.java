package com.herethere.withus.notification.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.notification.dto.response.NotificationCursorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RequestMapping("/api")
@Tag(name = "알림 API", description = "알림 기록 조회")
public interface NotificationApi {

	@Operation(
		summary = "알림 목록 조회",
		description = """
			내가 받은 알림 목록을 조회합니다.
			- 커서 기반 페이지네이션을 사용합니다.
			- 응답에서 받은 nextCursor 값을 그대로 cursor에 넣어 요청하면 됩니다.
			- push 값이 null이면 이동할 화면이 없습니다.

			**알림 타입별 push 경로**

			| 알림 타입 | 설명 | push |
			|---|---|---|
			| POKE | 상대방이 사진을 기다리고 있을 때 | null |
			| QUESTION_GENERATED | 오늘의 랜덤 질문이 생성됐을 때 | /today_question |
			| QUESTION_ANSWERED | 상대방이 오늘의 질문에 답했을 때 | /today_question |
			| KEYWORD_ANSWERED | 상대방이 키워드 사진을 보냈을 때 | /today_keyword/{coupleKeywordId} |
			"""
	)
	@GetMapping("/me/notifications")
	ResponseEntity<ApiResponse<NotificationCursorResponse>> getNotifications(
		@Parameter(
			description = "한 번에 조회할 알림 개수 (기본값: 20, 최소 1, 최대 50)",
			example = "20"
		)
		@RequestParam(defaultValue = "20")
		@Min(1)
		@Max(50)
		int size,

		@Parameter(
			description = "다음 페이지 조회를 위한 커서 값 (첫 페이지 조회 시 생략)"
		)
		@RequestParam(required = false)
		String cursor
	);
}
