package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

import com.herethere.withus.archive.enums.ArchiveType;

public record ArchiveDayDto(
	LocalDate date,

	ArchiveType archiveType,
	Long sourceId,

	String meImageKey,
	String partnerImageKey
) {
}
