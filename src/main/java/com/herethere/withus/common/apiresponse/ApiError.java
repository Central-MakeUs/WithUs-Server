package com.herethere.withus.common.apiresponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiError {
	private final String message;
	private final String code;
}
