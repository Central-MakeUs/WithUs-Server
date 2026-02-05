package com.herethere.withus.memory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.memory.api.MemoryApi;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemoryController implements MemoryApi {

	@Override
	public ResponseEntity<ApiResponse<MonthMemoryResponse>> getMonthMemories(Integer monthKey) {
		return null;
	}
}
