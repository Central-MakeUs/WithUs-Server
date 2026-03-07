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
import com.herethere.withus.auth.dto.request.RefreshTokenRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.dto.response.RefreshTokenResponse;
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
			- 응답이 401이고, error의 code가 EXPIRED_JWT_TOKEN일 경우, refresh api를 통해 accessToken과 refreshToken을 다시 받아야합니다.
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

	@Operation(
		summary = "토큰 재발급 API",
		description = """
			응답이 401이고, error의 code가 EXPIRED_JWT_TOKEN일 경우, 즉 AccessToken이 만료되었을 때, RefreshToken을 사용하여 새로운 토큰을 발급받습니다.
			- 요청 시 Body에 refreshToken을 담아 보냅니다.
			- Redis에 저장된 토큰과 대조하여 유효성을 확인합니다.
			- 새로운 AccessToken과 새로운 RefreshToken을 응답합니다. (Refresh Token Rotation)
			- 한 번 사용한 RefreshToken은 더 이상 사용할 수 없으므로 새로운 RefreshToken만을 사용해야 합니다.
			- 만약 해당 API의 응답이 401이고 error의 code가 EXPIRED_JWT_TOKEN 혹은 REFRESH_TOKEN_NOT_FOUND 인 경우, 사용할 수 없는 refreshToken이므로 소셜로그인을 다시 진행해서 새로운 토큰을 발급받아야합니다.
			"""
	)
	@PostMapping("/refresh")
	ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
		@Valid @RequestBody RefreshTokenRequest request);

	// @Operation(summary = "임시 토큰 발급2", description = "해당 id를 가진 유저에 대한 임시 토큰을 발급합니다.")
	// @PostMapping("/temp/token2/{id}")
	// ResponseEntity<ApiResponse<LoginResponse>> generateTempToken2(@PathVariable Long id);
	//
	// @Operation(summary = "임시 토큰 발급", description = "temp 유저에 대한 임시 토큰을 발급합니다.")
	// @PostMapping("/temp/token/{id}")
	// ResponseEntity<ApiResponse<LoginResponse>> generateTempToken(@PathVariable String id,
	// 	@RequestParam String fcmToken);
	//
	// @Operation(summary = "알림 기능 확인 api", description = "알림 기능 체크용 입니다.")
	// @PostMapping("/temp/notification")
	// ResponseEntity<ApiResponse<Void>> checkNotification(@RequestParam String fcmToken);
}
