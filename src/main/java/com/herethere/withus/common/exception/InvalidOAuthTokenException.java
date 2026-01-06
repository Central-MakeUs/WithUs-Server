package com.herethere.withus.common.exception;

public class InvalidOAuthTokenException extends BaseException{
	public InvalidOAuthTokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
