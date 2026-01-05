package com.herethere.withus.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.auth.api.AuthApi;
import com.herethere.withus.auth.dto.request.KaKaoLoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(KaKaoLoginRequest request) {
		return null;
	}
}
