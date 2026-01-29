package com.herethere.withus.archive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.archive.api.ArchiveApi;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.service.ArchiveService;
import com.herethere.withus.common.apiresponse.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ArchiveController implements ArchiveApi {

	private final ArchiveService archiveService;

	@Override
	public ResponseEntity<ApiResponse<ArchiveListResponse>> getArchivesByCursor(int size, String cursor) {
		ArchiveListResponse response = archiveService.getArchivesByCursor(cursor, size);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
