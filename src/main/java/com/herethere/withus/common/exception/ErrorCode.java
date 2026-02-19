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
	EXTERNAL_API_EXCEPTION(502, "외부 서비스 호출 중 오류가 발생했습니다."),

	// ObjectMapper 관련
	INVALID_FORMAT(400, "잘못된 형식의 데이터입니다. 파싱에 실패했습니다."),

	// USER
	USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
	NOT_YOUR_PARTNER(400, "같은 커플인 사용자가 아닙니다."),
	USER_ALREADY_INITIALIZED(409, "이미 초기 설정이 완료된 사용자입니다."),
	USER_NOT_INITIALIZED(409, "초기 설정이 완료되지 않은 사용자입니다."),
	USER_ALREADY_DELETED(409, "이미 탈퇴한 사용자입니다."),
	USER_DELETED(403, "탈퇴한 사용자입니다."),

	// COUPLE
	COUPLE_ALREADY_EXISTS(409, "사용자가 이미 커플이 존재합니다."),
	COUPLE_NOT_INITIALIZED(409, "커플 기본 설정이 완료되지 않았습니다."),
	COUPLE_ALREADY_INITIALIZED(409, "커플 기본 설정이 이미 완료되었습니다."),
	COUPLE_DELETED(409, "커플이 해지된 상태입니다."),
	COUPLE_NOT_FOUND(404, "해당 커플을 찾을 수 없습니다."),
	COUPLE_NOT_ACTIVE(409, "커플이 해지된 상태입니다."),
	COUPLE_KEYWORD_NOT_FOUND(404, "커플이 해당 키워드를 선택하지 않았습니다."),
	COUPLE_ALREADY_TERMINATED(403, "이미 탈퇴한 커플입니다."),

	// QUESTION
	QUESTION_NOT_FOUND(404, "해당 질문이 존재하지 않습니다."),
	COUPLE_QUESTION_NOT_FOUND(404, "커플의 해당 질문이 존재하지 않습니다."),
	PICTURE_ALREADY_UPLOADED(409, "이미 사진을 업로드 했습니다."),
	NOT_TODAY_QUESTION(400, "지난 질문에는 사진을 추가할 수 없습니다."),
	CANNOT_VIEW_TODAY_QUESTION(403, "해당 날짜의 사진을 조회할 수 없습니다."),

	// KEYWORD
	KEYWORD_NOT_FOUND(404, "해당 키워드가 존재하지 않습니다."),
	NOT_VALID_KEYWORD_COUNT(409, "잘못된 키워드 개수입니다."),

	// FOUR_CUT
	FOUR_CUT_NOT_FOUND(404, "해당 네컷 사진이 존재하지 않습니다."),

	// ARCHIVE
	INVALID_ARCHIVE_TYPE(500, "아카이브 타입이 올바르지 않습니다."),
	ARCHIVE_TYPE_NULL(500, "아카이브 타입이 null 입니다."),
	ARCHIVE_PICTURE_NOT_FOUND(404, "삭제할 사진이 존재하지 않습니다."),
	CANNOT_DELETE_TODAY_ARCHIVE(403, "오늘 날짜의 사진은 삭제할 수 없습니다."),

	//MEMORY
	WRONG_DATE(400, "해당 날짜는 불가능합니다."),
	FUTURE_DATE_NOT_ALLOWED(400, "미래 날짜는 불가능합니다."),
	MEMORY_ALREADY_UPLOADED(409, "이미 추억 사진을 업로드 했습니다."),
	MEMORY_NOT_FOUND(404, "해당 추억이 존재하지 않습니다."),

	// INVITATION_CODE
	CODE_GENERATE_FAILED(503, "중복으로 인해 초대 코드 생성을 실패했습니다."),
	CODE_NOT_FOUND(404, "초대 코드를 찾을 수 없습니다."),
	INVITED_SAME_USER(409, "자기 자신을 초대할 수 없습니다."),

	// OAUTH
	PROVIDER_NOT_FOUND(404, "존재하지 않는 OAuth Provider 입니다."),

	// BAD_REQUEST
	INVALID_INPUT(400, "잘못된 입력입니다."),

	// ACCESS_DENIED
	ACCESS_DENIED(403, "잘못된 접근입니다."),

	// AUTH
	UNAUTHENTICATED_USER(401, "인증되지 않은 사용자입니다."),
	APPLE_PUBLIC_KEY_ERROR(401, "애플 public Key를 찾을 수 없습니다."),
	APPLE_TOKEN_VALIDATION_ERROR(401, "애플 로그인에서 해당 토큰을 검증할 수 없습니다."),
	REFRESH_TOKEN_NOT_FOUND(401, "리프레시 토큰이 유효하지 않습니다."),

	// CURSOR
	INVALID_CURSOR(400, "잘못된 커서 요청입니다."),
	CURSOR_ENCODING_FAILED(500, "커서 인코딩을 실패했습니다."),

	// IMAGE
	NEED_IMAGE_KEY(400, "이미지 키가 존재하지 않습니다."),
	WRONG_IMAGE_FORMAT(400, "이미지가 형식에 맞지 않습니다."),
	WRONG_IMAGE_KEY(403, "잘못된 이미지 키입니다.");


	private final int status;
	private final String message;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}

