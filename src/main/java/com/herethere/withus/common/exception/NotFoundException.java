package com.herethere.withus.common.exception;

public class NotFoundException extends BaseException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
