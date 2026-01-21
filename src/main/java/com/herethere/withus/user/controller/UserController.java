package com.herethere.withus.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.user.api.UserApi;
import com.herethere.withus.user.dto.request.UserOnboardingRequest;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.OnboardingStatusResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;
import com.herethere.withus.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
	private final UserService userService;

	@Override
	public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(UserUpdateRequest userUpdateRequest) {
		UserUpdateResponse response = userService.updateUserProfile(userUpdateRequest);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<InvitationCodeResponse>> generateInvitationCode() {
		InvitationCodeResponse response = userService.generateInvitationCode();
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<OnboardingStatusResponse>> getOnboardingStatus() {
		OnboardingStatusResponse response = userService.getOnboardingStatus();
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> pokeUser(Long userId) {
		userService.pokeUser(userId);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> onboardUser(UserOnboardingRequest userOnboardingRequest) {
		return null;
	}
}
