package com.herethere.withus.archive.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
		@Schema(description = "질문 내용", example = "오늘의 밥타임 사진은?")
		String question,

		@Schema(description = "나의 사진 정보, 없으면 null", nullable = true)
		ImageInfo myInfo,

		@Schema(description = "상대방의 사진 정보, 없으면 null", nullable = true)
		ImageInfo partnerInfo
	) {
	}

	@Builder
	@Schema(description = "사진 정보")
	public record ImageInfo(
		@Schema(description = "사용자 고유 id", example = "123")
		Long userId,

		@Schema(description = "사용자 닉네임", example = "김철수")
		String name,

		@Schema(description = "프로필 썸네일 이미지 URL", example = "https://s3.com/profile/1.jpg", nullable = true)
		String profileThumbnailImageUrl,

		@Schema(description = "오늘 업로드한 사진 URL (미업로드 시 null)", example = "https://s3.com/question/101.jpg", nullable = true)
		String questionImageUrl,

		@Schema(type = "string", pattern = "HH:mm", example = "14:12", description = "인증 사진 업로드 시각 (UTC 기준, 미업로드 시 null)", nullable = true)
		@JsonFormat(pattern = "HH:mm", timezone = "UTC")
		LocalDateTime answeredAt
	) {
	}
}

