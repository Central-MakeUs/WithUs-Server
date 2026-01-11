package com.herethere.withus.s3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.s3.api.S3Api;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;
import com.herethere.withus.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class S3Controller implements S3Api {

	private final S3Service s3Service;

	@Override
	public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(PresignedUrlRequest request) {
		PresignedUrlResponse response = s3Service.createPresignedUrlResponse(request);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
