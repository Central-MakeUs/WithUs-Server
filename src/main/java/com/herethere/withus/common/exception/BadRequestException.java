package com.herethere.withus.common.exception;

public class BadRequestException extends BaseException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
