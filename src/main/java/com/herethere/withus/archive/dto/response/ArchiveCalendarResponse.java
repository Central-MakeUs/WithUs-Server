package com.herethere.withus.archive.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보관 캘린더 월 조회 응답")
public record ArchiveCalendarResponse(

	@Schema(description = "조회 연도", example = "2026")
	int year,

	@Schema(description = "조회 월", example = "1")
	int month,

	@Schema(description = "날짜별 캘린더 데이터")
	List<ArchiveCalendarDay> days
) {
	@Schema(description = "캘린더 날짜 단위 정보")
	public record ArchiveCalendarDay(

		@Schema(description = "날짜 (YYYY-MM-DD)", example = "2026-01-23")
		LocalDate date,

		@Schema(description = "내 사진 정보 (없으면 null)")
		String meImageThumbnailUrl,

		@Schema(description = "상대방 사진 정보 (없으면 null)")
		String partnerImageThumbnailUrl
	) {
	}
}
