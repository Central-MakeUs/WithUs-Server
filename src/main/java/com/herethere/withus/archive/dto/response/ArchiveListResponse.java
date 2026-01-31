package com.herethere.withus.archive.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.herethere.withus.archive.enums.ArchiveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보관함 사진 목록 응답 (무한 스크롤 포함)")
public record ArchiveListResponse(
	@Schema(description = "보관된 사진 날짜별 리스트")
	List<ArchiveInfo> archiveList,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext,

	@Schema(description = "다음 조회를 위한 커서 값 (없으면 null)", example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTIzVDAxOjExOjUyIiwiaWQiOjExOX0")
	String nextCursor
) {
	@Schema(description = "날짜별 커플 사진 정보")
	public record ArchiveInfo(
		@Schema(description = "사진이 기록된 날짜, 추후 상세정보 요청 시 사용됩니다.", example = "2026-01-28")
		LocalDate date,

		@Schema(description = "해당 날짜에서 조회 가능한 이미지 정보들 입니다.")
		List<ImageInfo> imageInfoList
	) {
	}

	@Schema(description = "사진 정보")
	public record ImageInfo(
		@Schema(description = "사진의 종류입니다. QUESTION, KEYWORD 중 하나입니다.", example = "QUESTION")
		ArchiveType archiveType,

		@Schema(description = "해당 엔티티의 id 값입니다. id만으로 식별하지 않고, archiveType과 id를 합쳐서 식별합니다.", example = "12")
		Long id,

		@Schema(description = "나의 사진 정보 (해당 날짜에 없으면 null)")
		String myImageUrl,

		@Schema(description = "상대방의 사진 정보 (해당 날짜에 없으면 null)")
		String partnerImageUrl
	) {
	}
}
