package com.herethere.withus.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.auth.api.AuthApi;
import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
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
	public ResponseEntity<ApiResponse<LoginResponse>> generateTempToken(String id, String fcmToken) {
		LoginResponse loginResponse = authService.generateTempToken(id, fcmToken);
		return ResponseEntity.ok(ApiResponse.success(loginResponse));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> checkNotification(String fcmToken) {
		fcmSendService.tempSendToToken(fcmToken, "확인용 알림 입니다.", "잘 작동 중입니다.", null);
		return ResponseEntity.ok(ApiResponse.success());
	}
}
