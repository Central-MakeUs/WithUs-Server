package com.herethere.withus.couple.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.couple.dto.request.CoupleInitializeRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/me/couple")
@Tag(name = "커플 API")
public interface CoupleApi {
	@Operation(summary = "초대 코드 입력 API-1", description = "초대 코드를 입력하여 초대자의 이름을 확인합니다. "
		+ "이후 따로 수락 버튼을 통해 초대를 실제로 받습니다.")
	@PostMapping("/join/preview")
	ResponseEntity<ApiResponse<CoupleJoinPreviewResponse>> checkCoupleJoinPreview(
		@Valid @RequestBody CoupleJoinPreviewRequest coupleJoinRequest
	);

	@Operation(summary = "초대 코드 입력 API-2", description = "실제로 초대를 받아 수락을 눌러 커플 관계가 맺어지는 API 입니다.")
	@PostMapping("/join")
	ResponseEntity<ApiResponse<CoupleJoinResponse>> joinCouple(
		@Valid @RequestBody CoupleJoinRequest coupleJoinRequest
	);

	@Operation(summary = "커플 기본 세팅 설정", description = "키워드와 시간을 설정합니다. 리스트는 NotNull 입니다. "
		+ "시간 형식은 HH:MM 입니다.")
	@PatchMapping("/settings")
	ResponseEntity<ApiResponse<Void>> initializeCoupleSettings(
		@Valid @RequestBody CoupleInitializeRequest coupleInitializeRequest
	);
}
