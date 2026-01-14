package com.herethere.withus.keyword.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@Tag(name = "키워드 API")
public interface KeywordApi {
	@Operation(summary = "전체 기본 키워드 목록 조회", description = "미리 설정된 기본 키워드를 조회합니다.")
	@GetMapping("/keywords")
	ResponseEntity<ApiResponse<DefaultKeywordsResponse>> getDefaultKeywords();

	@Operation(summary = "커플 키워드 목록 조회", description = "커플이 설정한 키워드 목록을 조회합니다.")
	@GetMapping("/me/couple/keywords")
	ResponseEntity<ApiResponse<CoupleKeywordsResponse>> getCoupleKeywords();
}
