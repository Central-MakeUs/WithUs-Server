package com.herethere.withus.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.auth.dto.request.KaKaoLoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.common.apiresponse.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "로그인 및 회원가입")
public interface AuthApi {

	@Operation(summary = "카카오 로그인")
	@PostMapping
	ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@Valid @RequestBody KaKaoLoginRequest request);
}
