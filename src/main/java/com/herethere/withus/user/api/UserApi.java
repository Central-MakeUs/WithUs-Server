package com.herethere.withus.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.OnboardingStatusResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api")
@Tag(name = "회원 API", description = "유저 프로필 관리 및 온보딩 상태 제어")
public interface UserApi {

	@Operation(
		summary = "유저 프로필 설정 및 수정",
		description = """
			유저의 닉네임, 프로필 사진 등을 설정합니다.
			- **회원가입 직후**: 이 API를 호출하여 초기 프로필을 완성해야 온보딩 다음 단계로 넘어갈 수 있습니다.
			- 이 API 호출 후에 User의 OnboardingStatus는 NEED_COUPLE_CONNECT가 됩니다.
			- 이후 언제든지 프로필 정보를 수정할 때 동일하게 사용합니다.
			"""
	)
	@PatchMapping("/me/user")
	ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
		@Valid @RequestBody UserUpdateRequest userUpdateRequest
	);

	@Operation(
		summary = "커플 초대 코드 생성",
		description = """
			상대방과 연결하기 위한 8자리 숫자 초대 코드를 생성합니다.
			- 본인이 생성한 코드를 상대방이 입력하면 커플 연결이 완료됩니다.
			"""
	)
	@PostMapping("/me/user/invitation-codes")
	ResponseEntity<ApiResponse<InvitationCodeResponse>> generateInvitationCode();

	@Operation(
		summary = "현재 온보딩 상태 조회",
		description = """
			사용자의 현재 진행 단계를 조회합니다.
			- 앱 실행 시 혹은 단계 전환 시 호출하여 어떤 화면(프로필 설정, 커플 연결, 메인)을 보여줄지 결정하세요.
			- 종류: NEED_USER_INITIAL_SETUP, NEED_COUPLE_CONNECT, NEED_COUPLE_INITIAL_SETUP, COMPLETED
			"""
	)
	@PostMapping("/me/status")
	ResponseEntity<ApiResponse<OnboardingStatusResponse>> getOnboardingStatus();
}
