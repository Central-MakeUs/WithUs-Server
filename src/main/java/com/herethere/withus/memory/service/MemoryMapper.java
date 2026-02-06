package com.herethere.withus.memory.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.herethere.withus.memory.domain.CustomMemory;
import com.herethere.withus.memory.domain.MemoryStatus;
import com.herethere.withus.memory.domain.MemoryType;
import com.herethere.withus.memory.domain.WeekMemory;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;
import com.herethere.withus.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemoryMapper {
	private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.KOREA);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");
	private final S3Service s3Service;

	public MonthMemoryResponse.MemorySummary toMemorySummary(WeekMemory weekMemory) {
		return MonthMemoryResponse.MemorySummary.builder()
			.memoryType(MemoryType.WEEK_MEMORY)
			.title(generateTitle(weekMemory))
			.weekEndDate(weekMemory.getWeekEndDate())
			.status(MemoryStatus.CREATED)
			.createdImageUrl(s3Service.createThumbnailImageUrl(weekMemory.getImageKey()))
			.createdAt(weekMemory.getWeekEndDate().atTime(LocalTime.MAX))
			.build();
	}

	public MonthMemoryResponse.MemorySummary toNeedCreateMemorySummary(List<String> imageKeys, LocalDate endDate) {
		List<String> imageUrls = imageKeys.stream()
			.map(s3Service::createThumbnailImageUrl)
			.toList();
		return MonthMemoryResponse.MemorySummary.builder()
			.memoryType(MemoryType.WEEK_MEMORY)
			.title(generateTitle(endDate))
			.weekEndDate(endDate)
			.status(MemoryStatus.NEED_CREATE)
			.needCreateImageUrls(imageUrls)
			.createdAt(endDate.atTime(LocalTime.MAX))
			.build();
	}

	public MonthMemoryResponse.MemorySummary toUnavailableMemorySummary(LocalDate endDate) {
		return MonthMemoryResponse.MemorySummary.builder()
			.memoryType(MemoryType.WEEK_MEMORY)
			.title(generateTitle(endDate))
			.weekEndDate(endDate)
			.status(MemoryStatus.UNAVAILABLE)
			.createdAt(endDate.atTime(LocalTime.MAX))
			.build();
	}

	public MonthMemoryResponse.MemorySummary toCustomMemorySummary(CustomMemory customMemory) {
		return MonthMemoryResponse.MemorySummary.builder()
			.memoryType(MemoryType.CUSTOM_MEMORY)
			.title(customMemory.getTitle())
			.customMemoryId(customMemory.getId())
			.status(MemoryStatus.CREATED)
			.createdImageUrl(s3Service.createThumbnailImageUrl(customMemory.getImageKey()))
			.createdAt(customMemory.getCreatedAt())
			.build();
	}

	private String generateTitle(WeekMemory memory) {
		LocalDate endDate = memory.getWeekEndDate();

		// 1. 종료일 기준 해당 월의 몇 번째 주인지 계산
		int weekOfMonth = endDate.get(WEEK_FIELDS.weekOfMonth());

		// 2. 제목 생성: "4월 2주 (03.29~04.04)"
		return String.format("%d월 %d주 (%s~%s)",
			endDate.getMonthValue(),
			weekOfMonth,
			memory.getWeekStartDate().format(DATE_FORMATTER),
			endDate.format(DATE_FORMATTER)
		);
	}

	private String generateTitle(LocalDate endDate) {
		LocalDate startDate = endDate.minusDays(6);
		// 1. 종료일 기준 해당 월의 몇 번째 주인지 계산
		int weekOfMonth = endDate.get(WEEK_FIELDS.weekOfMonth());

		// 2. 제목 생성: "4월 2주 (03.29~04.04)"
		return String.format("%d월 %d주 (%s~%s)",
			endDate.getMonthValue(),
			weekOfMonth,
			startDate.format(DATE_FORMATTER),
			endDate.format(DATE_FORMATTER)
		);
	}
}
