package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

public interface DailyArchiveView {
	LocalDate getArchiveDate();

	String getMeImageKey();

	String getPartnerImageKey();
}
