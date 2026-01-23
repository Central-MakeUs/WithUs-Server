package com.herethere.withus.common.dto.internal;

import java.time.LocalDateTime;

public record CursorPayload(LocalDateTime createdAt, Long id) {
}
