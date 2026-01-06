package com.herethere.withus.common.exception;

public class ExternalApiException extends BaseException {
	public ExternalApiException(ErrorCode errorCode) {
		super(errorCode);
	}
}
