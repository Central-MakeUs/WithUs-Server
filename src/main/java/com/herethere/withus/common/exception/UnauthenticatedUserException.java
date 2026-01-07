package com.herethere.withus.common.exception;

public class UnauthenticatedUserException extends BaseException {
	public UnauthenticatedUserException(ErrorCode errorCode) {
		super(errorCode);
	}
}
