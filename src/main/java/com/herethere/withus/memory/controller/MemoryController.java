package com.herethere.withus.memory.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.memory.api.MemoryApi;
import com.herethere.withus.memory.domain.MemoryType;
import com.herethere.withus.memory.dto.request.CustomMemoryCreateRequest;
import com.herethere.withus.memory.dto.request.MemoryCreateRequest;
import com.herethere.withus.memory.dto.response.MemoryDetailResponse;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;
import com.herethere.withus.memory.dto.response.WeekMemoryCreateResponse;
import com.herethere.withus.memory.service.MemoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemoryController implements MemoryApi {

	private final MemoryService memoryService;

	@Override
	public ResponseEntity<ApiResponse<MonthMemoryResponse>> getMonthMemories(int monthKey) {
		MonthMemoryResponse response = memoryService.getMonthMemories(monthKey);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<WeekMemoryCreateResponse>> createMemory(LocalDate weekEndDate, MemoryCreateRequest request) {
		WeekMemoryCreateResponse response = memoryService.createMemory(request, weekEndDate);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> createCustomMemory(CustomMemoryCreateRequest request) {
		memoryService.createCustomMemory(request);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Override
	public ResponseEntity<ApiResponse<MemoryDetailResponse>> getMemoryDetail(MemoryType memoryType,
		LocalDate weekEndDate, Long targetId) {
		MemoryDetailResponse response = memoryService.getMemoryDetail(memoryType, weekEndDate, targetId);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
