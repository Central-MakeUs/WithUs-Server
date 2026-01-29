package com.herethere.withus.couple.domain;

public enum OnboardingStatus {
	// 1. 본인의 기본 설정을 아직 안 함
	NEED_USER_INITIAL_SETUP,

	// 2. 커플 연결 자체가 안 되어 있음
	NEED_COUPLE_CONNECT,

	// 4. 모든 설정 완료 (메인 서비스 이용 가능)
	COMPLETED
}
