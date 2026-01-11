package com.herethere.withus.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/me/user")
@Tag(name = "회원 API")
public interface UserApi {
	@Operation(summary = "유저 정보 수정 API", description = "유저 정보 수정 및 회원가입 이후 이 API로 초기 설정을 합니다.")
	@PatchMapping
	ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
		@Valid @RequestBody UserUpdateRequest userUpdateRequest
	);

	@Operation(summary = "초대 코드 생성")
	@PostMapping("/invitation-codes")
	ResponseEntity<ApiResponse<InvitationCodeResponse>> generateInvitationCode();
}
