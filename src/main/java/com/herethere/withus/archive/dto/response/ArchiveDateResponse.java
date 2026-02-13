package com.herethere.withus.archive.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.herethere.withus.archive.enums.ArchiveType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "키워드 사진 조회 응답")
public record ArchiveDateResponse(
	@Schema(description = "사진이 기록된 날짜", example = "2026-01-28")
	LocalDate date,

	@Schema(description = "한 날짜의 사진 목록들")
	List<ArchiveInfo> archiveInfoList
) {
	@Builder
	@Schema(description = "하나의 보관 이미지")
	public record ArchiveInfo(
		@Schema(description = "사진의 종류입니다. QUESTION, KEYWORD 중 하나입니다.", example = "QUESTION")
		ArchiveType archiveType,

		@Schema(description = "해당 엔티티의 id 값입니다. id만으로 식별하지 않고, archiveType과 id를 합쳐서 식별합니다.", example = "12")
		Long id,

		@Schema(description = "질문 내용", example = "오늘의 밥타임 사진은?")
		String question,

		@Schema(description = "나의 사진 정보", nullable = true)
		ImageInfo myInfo,

		@Schema(description = "상대방의 사진 정보", nullable = true)
		ImageInfo partnerInfo,

		@Schema(description = "선택 여부. true 라면, 리스트를 보여줄 때 해당 칸으로 가 있어야함.")
		boolean selected
	) {
	}

	@Builder
	@Schema(description = "사진 정보")
	public record ImageInfo(
		@Schema(description = "사용자 고유 id", example = "123")
		Long userId,

		@Schema(description = "사용자 닉네임", example = "김철수")
		String name,

		@Schema(description = "프로필 썸네일 이미지 URL (미업로드 시 null)", example = "https://s3.com/profile/1.jpg", nullable = true)
		String profileThumbnailImageUrl,

		@Schema(description = "오늘 업로드한 사진 URL (미업로드 시 null)", example = "https://s3.com/question/101.jpg", nullable = true)
		String answerImageUrl,

		@Schema(type = "string", pattern = "HH:mm", example = "14:12", description = "인증 사진 업로드 시각 (Asia/Seoul 기준, 미업로드 시 null)", nullable = true)
		@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
		LocalDateTime answeredAt
	) {
	}
}

