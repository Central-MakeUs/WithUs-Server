package com.herethere.withus.keyword.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.keyword.dto.request.TodayKeywordImageRequest;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.dto.response.TodayKeywordResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api")
@Tag(name = "키워드 API", description = "시스템 공통 키워드 및 커플별 오늘의 미션 관리")
public interface KeywordApi {

	@Operation(
		summary = "전체 기본 키워드 목록 조회",
		description = "서비스에서 제공하는 기본 키워드 리스트를 조회합니다. 커플 초기 설정 시 선택 목록을 뿌려줄 때 사용합니다."
	)
	@GetMapping("/keywords")
	ResponseEntity<ApiResponse<DefaultKeywordsResponse>> getDefaultKeywords();

	@Operation(
		summary = "우리 커플이 설정한 키워드 목록 조회",
		description = """
			온보딩 시 우리 커플이 선택한 기본 키워드 + 직접 입력한 키워드 목록을 모두 조회합니다.
			상단의 "오늘의 질문" 옆에 뜰 키워드를 조회할 때 사용합니다.
			"""
	)
	@GetMapping("/me/couple/keywords")
	ResponseEntity<ApiResponse<CoupleKeywordsResponse>> getCoupleKeywords();

	@Operation(
		summary = "오늘의 키워드 상세 조회",
		description = """
			해당 키워드에 대해 질문과 나/상대방의 사진을 조회합니다.
			- coupleKeywordId: `/me/couple/keywords` 에서 받아온 커플 키워드의 고유 ID입니다.
			- 응답의 myInfo / partnerInfo를 통해 사진을 확인할 수 있습니다.
			- Info의 profileImageUrl이 null이면 앱의 기본이미지로 대체합니다.
			- 사진을 올리지 않으면 Info의 questionImageUrl과 answeredAt이 null입니다.
			"""
	)
	@GetMapping("/me/couple/keywords/{coupleKeywordId}/today")
	ResponseEntity<ApiResponse<TodayKeywordResponse>> getTodayCoupleKeyword(
		@Parameter(description = "커플 키워드 고유 ID", example = "10")
		@PathVariable Long coupleKeywordId
	);

	@Operation(
		summary = "키워드 사진 업로드",
		description = """
			해당 키워드에 해당하는 사진을 업로드합니다.
			- coupleKeywordId: `/me/couple/keywords` 에서 받아온 커플 키워드의 고유 ID입니다.
			- `/api/images/presigned-url`에서 response로 받은 ImageKey를 보내야 합니다.
			- 이미 사진을 업로드한 경우 실패합니다.
			"""
	)
	@PostMapping("/me/couple/keywords/{coupleKeywordId}/today/image")
	ResponseEntity<ApiResponse<Void>> uploadTodayCoupleKeywordPicture(
		@Parameter(description = "커플 키워드 고유 ID", example = "10")
		@PathVariable Long coupleKeywordId,
		@Valid @RequestBody TodayKeywordImageRequest request
	);
}
