package com.herethere.withus.couple.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.couple.api.CoupleApi;
import com.herethere.withus.couple.dto.request.CoupleInitializeRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;
import com.herethere.withus.couple.service.CoupleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CoupleController implements CoupleApi {
	private final CoupleService coupleService;

	@Override
	public ResponseEntity<ApiResponse<CoupleJoinPreviewResponse>> checkCoupleJoinPreview(
		CoupleJoinPreviewRequest coupleJoinRequest) {
		CoupleJoinPreviewResponse response = coupleService.checkCoupleJoinPreview(coupleJoinRequest);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<CoupleJoinResponse>> joinCouple(CoupleJoinRequest coupleJoinRequest) {
		CoupleJoinResponse response = coupleService.joinCouple(coupleJoinRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> initializeCoupleSettings(
		CoupleInitializeRequest coupleInitializeRequest) {
		coupleService.initializeCoupleSettings(coupleInitializeRequest);
		return ResponseEntity.ok((ApiResponse.success()));
	}
}
