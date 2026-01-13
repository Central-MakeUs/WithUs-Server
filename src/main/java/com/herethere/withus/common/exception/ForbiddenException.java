package com.herethere.withus.common.exception;

public class ForbiddenException extends BaseException {
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
