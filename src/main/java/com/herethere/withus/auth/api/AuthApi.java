package com.herethere.withus.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.request.LogoutRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.common.apiresponse.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "로그인 및 회원가입")
public interface AuthApi {

	@Operation(
		summary = "소셜 로그인 및 회원가입",
		description = """
			소셜 로그인을 진행합니다.
			응답의 onboardingStatus를 확인하여 다음 화면을 결정합니다.
			- 1시간 유효한 AccessToken과 28일 유효한 refreshToken을 응답합니다.
			"""
	)
	@PostMapping("/login/{provider}")
	ResponseEntity<ApiResponse<LoginResponse>> login(
		@Parameter(
			schema = @Schema(allowableValues = {"kakao", "google", "apple"})
		)
		@PathVariable String provider,
		@Valid @RequestBody LoginRequest request);

	@Operation(summary = "로그아웃 API",
		description = """
			로그아웃 할 때 사용합니다.
			- fcmToken을 받아, 해당 토큰을 무효화합니다.
			- refreshToken을 무효화합니다.
			""")
	@PostMapping("/logout")
	ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request);

	@Operation(summary = "임시 토큰 발급", description = "temp 유저에 대한 임시 토큰을 발급합니다.")
	@PostMapping("/temp/token/{id}")
	ResponseEntity<ApiResponse<LoginResponse>> generateTempToken(@PathVariable String id,
		@RequestParam String fcmToken);

	@Operation(summary = "알림 기능 확인 api", description = "알림 기능 체크용 입니다.")
	@PostMapping("/temp/notification")
	ResponseEntity<ApiResponse<Void>> checkNotification(@RequestParam String fcmToken);
}
