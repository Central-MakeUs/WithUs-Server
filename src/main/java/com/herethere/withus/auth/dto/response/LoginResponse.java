package com.herethere.withus.auth.dto.response;

import com.herethere.withus.couple.domain.OnboardingStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
	@Schema(description = "액세스 토큰 (만료시간 1시간)")
	String jwt,
	@Schema(description = "리프레시 토큰 (만료시간 28일)")
	String refreshToken,
	@Schema(
		description = """
			현재 사용자의 온보딩 상태. 이 값에 따라 진입 화면을 결정합니다.
			- NEED_USER_INITIAL_SETUP: 유저 프로필 설정 필요 (회원가입 화면 진입)
			- NEED_COUPLE_CONNECT: 커플 연결 필요 (초대 링크 화면 진입)
			- NEED_COUPLE_INITIAL_SETUP: 커플 설정 필요 (커플 설정 페이지 진입)
			- COMPLETED: 모든 설정 완료 (메인 화면 진입)
			""",
		example = "NEED_COUPLE_CONNECT"
	)
	OnboardingStatus onboardingStatus) {
}
