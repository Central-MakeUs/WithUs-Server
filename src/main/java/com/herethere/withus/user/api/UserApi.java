package com.herethere.withus.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.user.dto.request.UserOnboardingRequest;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.OnboardingStatusResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
			- 만약 유저가 프로필 사진을 선택하지 않았다면, null로 관리합니다.
			- 추후 응답에서도 profileImageUrl 이 null 이라면 앱에 존재하는 기본 이미지로 대체합니다.
			- 기획 변경에 따라 추후 수정 예정입니다. (회원가입 시에는 /api/me/onboarding을 사용합니다.)
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

	@Operation(
		summary = "콕 찌르기",
		description = """
			커플 상대를 콕 찔러서 알림을 보냅니다.
			- 오늘의 질문, 키워드 모두 같은 API를 사용하여 콕 찌르기를 실행합니다.
			- 오늘의 질문이나 키워드 사진 조회에서 Info에 포함된 userId로 실행시킵니다.
			"""
	)
	@PostMapping("/users/{userId}/poke")
	ResponseEntity<ApiResponse<Void>> pokeUser(
		@Parameter(description = "콕 찌르기 대상 고유 ID", example = "505")
		@PathVariable Long userId);

	@Operation(
		summary = "초기 회원가입 시 정보 입력 API",
		description = """
			초기 회원 가입시 정보를 입력합니다.
			- **User의 OnboardingStatus가 NEED_USER_INITIAL_SETUP일 때**: 이 API를 호출하여 초기 프로필을 완성해야 온보딩 다음 단계로 넘어갈 수 있습니다.
			- 이 API 호출 후에 User의 OnboardingStatus는 NEED_COUPLE_CONNECT가 됩니다.
			- 만약 유저가 프로필 사진을 선택하지 않았다면, null로 관리합니다.
			- 추후 응답에서도 profileImageUrl 이 null 이라면 앱에 존재하는 기본 이미지로 대체합니다.
			"""
	)
	@PutMapping("/me/onboarding")
	ResponseEntity<ApiResponse<Void>> onboardUser(
		@Valid @RequestBody UserOnboardingRequest userOnboardingRequest
	);
}
