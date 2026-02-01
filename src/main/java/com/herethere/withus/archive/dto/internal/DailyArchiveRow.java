package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

public record DailyArchiveRow(
	LocalDate archiveDate,
	String meImageKey,
	String partnerImageKey
) {
}
