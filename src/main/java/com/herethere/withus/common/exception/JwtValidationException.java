package com.herethere.withus.common.exception;

public class JwtValidationException extends BaseException {
	public JwtValidationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
