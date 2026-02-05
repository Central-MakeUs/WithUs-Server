package com.herethere.withus.memory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.memory.api.MemoryApi;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;
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
}
