package com.herethere.withus.archive.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.archive.api.ArchiveApi;
import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.enums.ArchiveType;
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

	@Override
	public ResponseEntity<ApiResponse<ArchiveDateResponse>> getArchiveByDate(LocalDate date, Long targetId,
		ArchiveType targetType) {
		ArchiveDateResponse response = archiveService.getArchiveByDate(date, targetId, targetType);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
