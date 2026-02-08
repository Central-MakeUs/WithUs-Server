package com.herethere.withus.memory.dto.response;

import java.time.LocalDate;

import com.herethere.withus.memory.domain.MemoryType;

public record WeekMemoryCreateResponse(MemoryType memoryType, LocalDate weekEndDate) {
}
