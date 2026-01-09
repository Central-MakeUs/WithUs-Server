package com.herethere.withus.common.exception;

public class ServiceUnavailableException extends BaseException {
	public ServiceUnavailableException(ErrorCode errorCode) {
		super(errorCode);
	}
}
