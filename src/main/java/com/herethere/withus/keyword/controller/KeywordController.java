package com.herethere.withus.keyword.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.keyword.api.KeywordApi;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KeywordController implements KeywordApi {

	@Override
	public ResponseEntity<ApiResponse<DefaultKeywordsResponse>> getDefaultKeywords() {
		return null;
	}

	@Override
	public ResponseEntity<ApiResponse<CoupleKeywordsResponse>> getCoupleKeywords() {
		return null;
	}
}
