package com.herethere.withus.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.herethere.withus.user.dto.response.UserOnboardingResponse;
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
			유저의 프로필을 수정합니다. (닉네임, 생일, 프로필사진)
			- `GET /me/user/profile`을 통해 현재 유저의 프로필을 받고, 입력칸을 채워서 유저에게 보여줘야 합니다.
			- 기본적으로는 입력받은 값으로 전부 덮어씌웁니다.(PUT)
			- 만약 유저가 프로필 사진을 변경하지 않았다면, isImageUpdated는 false이고, imageKey는 null입니다.
			- 만약 유저가 프로필 사진을 없애는 것으로 변경했다면, isImageUpdated는 true이고, imageKey는 null입니다.
			- 추후 응답에서도 profileImageUrl 이 null 이라면 앱에 존재하는 기본 이미지로 대체합니다.
			"""
	)
	@PutMapping("/me/user/profile")
	ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
		@Valid @RequestBody UserUpdateRequest userUpdateRequest
	);

	@Operation(
		summary = "유저 프로필 조회",
		description = """
			유저의 프로필을 조회합니다.
			- `POST /me/user/profile`에서 입력칸을 채우기 위해 사용합니다.
			- 만약 유저가 프로필 사진을 선택하지 않았다면, null로 관리합니다.
			- 응답에서도 profileImageUrl 이 null 이라면 앱에 존재하는 기본 이미지로 대체합니다.
			"""
	)
	@GetMapping("/me/user/profile")
	ResponseEntity<ApiResponse<UserUpdateResponse>> getUserProfile();

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
			- 종류: NEED_USER_INITIAL_SETUP, NEED_COUPLE_CONNECT, COMPLETED
			"""
	)
	@GetMapping("/me/status")
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
	ResponseEntity<ApiResponse<UserOnboardingResponse>> onboardUser(
		@Valid @RequestBody UserOnboardingRequest userOnboardingRequest
	);

	@Operation(
		summary = "회원 탈퇴 API",
		description = """
		현재 로그인한 사용자의 계정을 탈퇴 처리합니다.
		- OAuth 로그인 사용자의 경우, 외부 인증 서버와의 연결을 함께 해제합니다.
		  - **KAKAO**: 카카오 계정과의 연결을 해제(unlink)합니다.
		  - **APPLE**: 애플 계정의 refresh token을 revoke 처리합니다.
		- 외부 인증 서버와의 연결 해제에 실패할 경우, **회원 탈퇴는 처리되지 않습니다.**
		- 회원 탈퇴는 **Soft Delete 방식**으로 처리되며, 탈퇴 이후 해당 계정으로는 더 이상 서비스 이용이 불가능합니다.
		- 탈퇴 완료 후, 동일한 OAuth 계정으로 재로그인 시 **신규 회원 가입 플로우**가 진행됩니다.
		"""
	)
	@DeleteMapping("/users/me")
	ResponseEntity<ApiResponse<Void>> withdrawUser();
}
