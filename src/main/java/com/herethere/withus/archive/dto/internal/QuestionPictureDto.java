package com.herethere.withus.archive.dto.internal;

import java.time.LocalDateTime;

public record QuestionPictureDto(
	String questionImageKey,
	LocalDateTime answeredAt
) {
}
