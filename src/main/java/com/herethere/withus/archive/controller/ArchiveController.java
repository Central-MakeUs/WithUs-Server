package com.herethere.withus.archive.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.archive.api.ArchiveApi;
import com.herethere.withus.archive.dto.request.ArchiveBulkDeleteRequest;
import com.herethere.withus.archive.dto.request.ArchiveDeleteRequest;
import com.herethere.withus.archive.dto.response.ArchiveCalendarResponse;
import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionDetailResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionListResponse;
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

	@Override
	public ResponseEntity<ApiResponse<ArchiveQuestionListResponse>> getArchiveQuestions(int size, String cursor) {
		ArchiveQuestionListResponse response = archiveService.getArchiveQuestions(size, cursor);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<ArchiveQuestionDetailResponse>> getDetailArchiveQuestion(Long coupleQuestionId) {
		ArchiveQuestionDetailResponse response = archiveService.getDetailArchiveQuestion(coupleQuestionId);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> bulkDeleteArchive(ArchiveBulkDeleteRequest request) {
		archiveService.bulkDeleteArchive(request.items());
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> deleteArchive(ArchiveDeleteRequest request) {
		archiveService.deleteArchive(request.archiveType(), request.id(), request.date());
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Override
	public ResponseEntity<ApiResponse<ArchiveCalendarResponse>> getArchiveCalendar(int year, int month) {
		ArchiveCalendarResponse response = archiveService.getArchiveCalendar(year, month);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
