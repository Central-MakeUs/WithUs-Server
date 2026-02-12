package com.herethere.withus.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.auth.api.AuthApi;
import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.request.LogoutRequest;
import com.herethere.withus.auth.dto.request.RefreshTokenRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.dto.response.RefreshTokenResponse;
import com.herethere.withus.auth.service.AuthService;
import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.notification.service.FcmSendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;
	private final FcmSendService fcmSendService;

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> login(@PathVariable String provider,
		LoginRequest request) {
		OAuthProviderType providerType = OAuthProviderType.from(provider);
		LoginResponse loginResponse = authService.login(request, providerType);
		return ResponseEntity.ok(ApiResponse.success(loginResponse));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> logout(LogoutRequest request) {
		authService.logout(request);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@Override
	public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(RefreshTokenRequest request) {
		RefreshTokenResponse response = authService.refresh(request);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> generateTempToken(Long id) {
		LoginResponse loginResponse = authService.generateTempToken(id);
		return ResponseEntity.ok(ApiResponse.success(loginResponse));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> checkNotification(String fcmToken) {
		fcmSendService.tempSendToToken(fcmToken, "확인용 알림 입니다.", "잘 작동 중입니다.", null);
		return ResponseEntity.ok(ApiResponse.success());
	}
}
