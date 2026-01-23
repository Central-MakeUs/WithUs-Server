package com.herethere.withus.common.exception;

public class InternalServerException extends BaseException {
	public InternalServerException(ErrorCode errorCode) {
		super(errorCode);
	}
}
