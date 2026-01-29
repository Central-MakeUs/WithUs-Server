package com.herethere.withus.couple.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.request.SetCoupleKeywordRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/me/couple")
@Tag(name = "커플 API", description = "커플 연결 및 초기 설정 관리")
public interface CoupleApi {
	@Operation(
		summary = "초대 코드 확인 (프리뷰)",
		description = """
			상대방이 보낸 초대 코드를 입력하여 초대 정보를 확인합니다.
			- 실제로 커플이 맺어지지는 않으며, 화면에 'jpg 님이 쏘피 님을 초대했어요!'를 띄우기 위한 용도입니다.
			- 반환된 데이터를 확인한 후, 사용자가 '초대 수락하기'를 누르면 `POST /api/me/couple/join`을 호출하여 실제로 커플을 맺습니다.
			"""
	)
	@PostMapping("/join/preview")
	ResponseEntity<ApiResponse<CoupleJoinPreviewResponse>> checkCoupleJoinPreview(
		@Valid @RequestBody CoupleJoinPreviewRequest coupleJoinRequest
	);

	@Operation(
		summary = "초대 코드 수락 (실제 연결)",
		description = """
			실제로 초대 코드를 수락하여 상대방과 커플 관계를 형성합니다.
			- 성공 시 두 유저는 하나의 Couple ID를 공유하게 됩니다.
			- 이후 온보딩 상태는 COMPLETE으로 변경됩니다.
			"""
	)
	@PostMapping("/join")
	ResponseEntity<ApiResponse<CoupleJoinResponse>> joinCouple(
		@Valid @RequestBody CoupleJoinRequest coupleJoinRequest
	);

	@Operation(
		summary = "커플 키워드 설정",
		description = """
			커플의 키워드를 설정합니다.
			- defaultKeywordIds: 커플이 선택한 시스템 기본 설정 키워드 ID 리스트
			- customKeywords: 커플이 커스텀으로 추가한 키워드 리스트 (ID가 없기 때문에 String으로 받습니다.)
			- 전체 키워드 합은 최소 1개, 최대 3개여야 합니다.
			"""
	)
	@PutMapping("/keywords")
	ResponseEntity<ApiResponse<Void>> setCoupleKeywords(
		@Valid @RequestBody SetCoupleKeywordRequest setCoupleKeywordRequest
	);
}
