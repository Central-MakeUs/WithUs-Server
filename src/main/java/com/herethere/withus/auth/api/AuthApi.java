package com.herethere.withus.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.common.apiresponse.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "로그인 및 회원가입")
public interface AuthApi {

	@Operation(summary = "Oauth 로그인")
	@PostMapping("/login/{provider}")
	ResponseEntity<ApiResponse<LoginResponse>> login(
		@Parameter(
			schema = @Schema(allowableValues = {"kakao", "google", "apple"})
		)
		@PathVariable String provider,
		@Valid @RequestBody LoginRequest request);

	@Operation(summary = "임시 토큰 발급", description = """
		temp 유저에 대한 임시 토큰을 발급합니다.
		""")
	@PostMapping("/temp/token")
	ResponseEntity<ApiResponse<LoginResponse>> generateTempToken();
}
