package com.herethere.withus.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.auth.domain.OauthProviderType;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.common.apiresponse.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "로그인 및 회원가입")
public interface AuthApi {

	@Operation(summary = "Oauth 로그인")
	@PostMapping("/login/{provider}")
	ResponseEntity<ApiResponse<LoginResponse>> login(@PathVariable OauthProviderType provider,
		@Valid @RequestBody LoginRequest request);
}
