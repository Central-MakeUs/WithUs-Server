package com.herethere.withus.common.exception;

public class AuthException extends BaseException {
	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
