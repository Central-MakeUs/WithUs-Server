package com.herethere.withus.common.exception;

public class ConflictException extends BaseException {
	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
