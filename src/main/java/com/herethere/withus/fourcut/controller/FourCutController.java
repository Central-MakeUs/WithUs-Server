package com.herethere.withus.fourcut.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.fourcut.api.FourCutApi;
import com.herethere.withus.fourcut.dto.request.FourCutUploadRequest;
import com.herethere.withus.fourcut.dto.response.FourCutCursorResponse;
import com.herethere.withus.fourcut.service.FourCutService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FourCutController implements FourCutApi {
	private final FourCutService fourCutService;

	@Override
	public ResponseEntity<ApiResponse<FourCutCursorResponse>> getFourCutsByCursor(int size, String cursor) {
		FourCutCursorResponse response = fourCutService.getFourCutsByCursor(size, cursor);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> uploadFourCutImage(FourCutUploadRequest fourCutUploadRequest) {
		return null;
	}
}
