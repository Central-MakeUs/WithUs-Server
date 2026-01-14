package com.herethere.withus.keyword.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.keyword.api.KeywordApi;
import com.herethere.withus.keyword.dto.request.TodayKeywordImageRequest;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.dto.response.TodayKeywordResponse;
import com.herethere.withus.keyword.service.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KeywordController implements KeywordApi {
	private final KeywordService keywordService;

	@Override
	public ResponseEntity<ApiResponse<DefaultKeywordsResponse>> getDefaultKeywords() {
		DefaultKeywordsResponse response = keywordService.getDefaultKeywords();
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<CoupleKeywordsResponse>> getCoupleKeywords() {
		CoupleKeywordsResponse response = keywordService.getCoupleKeywords();
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<TodayKeywordResponse>> getTodayCoupleKeyword(Long coupleKeywordId) {
		return null;
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> uploadTodayCoupleKeywordPicture(Long coupleKeywordId,
		TodayKeywordImageRequest request) {
		return null;
	}
}
