package com.herethere.withus.memory.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.herethere.withus.memory.domain.MemoryStatus;
import com.herethere.withus.memory.domain.MemoryType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 추억 목록 조회 응답")
public record MonthMemoryResponse(
	@Schema(description = "조회 대상 연월 (YYYYMM 형식)", example = "202604")
	int monthKey,

	@Schema(description = "해당 월의 주차별 요약 정보 리스트")
	List<MemorySummary> weekMemorySummaries
) {
	@Schema(description = "주차별 추억 요약 정보")
	public record MemorySummary(
		@Schema(description = "추억 종류 (주차별 자동 생성 or 유저 커스텀 생성)", example = "WEEK_MEMORY")
		MemoryType memoryType,

		@Schema(description = "띄울 제목", example = "4월 2주 (03.29~04.04)")
		String title,

		@Schema(description = "memoryType이 custom일 경우 사용하는 id", example = "11")
		Long customMemoryId,

		@Schema(description = "memoryType이 week일 경우 사용, 한 주 종료의 날짜 (토요일)", example = "2026-02-07")
		LocalDate weekEndDate,

		@Schema(description = "추억 기록 상태 (NEED_CREATE: 업로드 필요, CREATED: 업로드 완료, DISABLED: 작성 불가)", example = "NEED_CREATE")
		MemoryStatus status,

		@Schema(description = "NEED_CREATE 상태일 때 노출할 이미지 URL 리스트. 그 외 상태에서는 null",
			example = "[\"https://s3.withus.com/guides/pose1.png\"]")
		List<String> needCreateImageUrls,

		@Schema(description = "CREATED 상태일 때 사용자가 업로드한 이미지 URL. 그 외 상태에서는 null",
			example = "https://s3.withus.com/memories/couple123_4w1.jpg")
		String createdImageUrl,

		@Schema(description = "생성된 시간(UTC)")
		LocalDateTime createdAt
	) {
	}
}
