package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

public record ArchiveDayRow(
	LocalDate date,
	String archiveType,
	Long sourceId,
	String meImageKey,
	String partnerImageKey
) {
}
