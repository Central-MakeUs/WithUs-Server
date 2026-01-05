package com.herethere.withus.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.auth.api.AuthApi;
import com.herethere.withus.auth.domain.OauthProviderType;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.service.AuthService;
import com.herethere.withus.common.apiresponse.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> login(@PathVariable OauthProviderType provider,
		LoginRequest request) {
		return null;
	}
}
