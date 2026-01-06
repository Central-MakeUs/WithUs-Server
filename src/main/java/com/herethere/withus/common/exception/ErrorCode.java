package com.herethere.withus.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// 서버 오류
	INTERNAL_SERVER_ERROR(500, "서버에 오류가 발생했습니다."),

	// JWT 토큰 관련
	EXPIRED_JWT_TOKEN(401, "만료된 JWT 토큰입니다."),
	INVALID_JWT_TOKEN(401, "JWT 토큰이 잘못되었습니다."),
	EMPTY_JWT_TOKEN(401, "헤더에 JWT 토큰이 없습니다."),

	// 외부 API 관련
	EMPTY_API_RESPONSE(502, "외부 API로부터 응답이 없습니다."),
	API_REQUEST_FAILED(502, "외부 API 요청에 실패했습니다."),
	OAUTH_TOKEN_ERROR(401, "OAuth 토큰 인증에 실패했습니다."),

	// ObjectMapper 관련
	INVALID_FORMAT(400, "잘못된 형식의 데이터입니다. 파싱에 실패했습니다."),

	// USER
	USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),

	// PROFILE
	PROFILE_ALREADY_EXISTS(409, "이미 프로필 작성을 완료한 사용자입니다."),

	// RELATIONSHIP
	RELATIONSHIP_NOT_FOUND(404, "존재하지 않는 관계입니다."),

	// PROFILE KEYWORD
	PROFILE_KEYWORD_NOT_FOUND(404, "존재하지 않는 프로필 키워드입니다."),

	// CATEGORY
	CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리입니다."),

	// CONVERSATION
	CONVERSATION_NOT_FOUND(404, "존재하지 않는 대화입니다."),
	CONVERSATION_CARD_NOT_FOUND(404, "존재하지 않는 대화 카드입니다."),
	CONVERSATION_KEYWORD_NOT_FOUND(404, "존재하지 않는 대화 키워드입니다."),
	CONVERSATION_REPORT_NOT_FOUND(404, "존재하지 않는 대화 리포트입니다."),

	// OAUTH
	PROVIDER_NOT_FOUND(404, "존재하지 않는 OAuth Provider 입니다."),;

	private final int status;
	private final String message;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}

