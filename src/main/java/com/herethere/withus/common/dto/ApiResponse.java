package com.herethere.withus.common.dto;

import com.herethere.withus.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
	private final boolean success;
	private final T data;
	private final ApiError error;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static <T> ApiResponse<T> success() {
		return new ApiResponse<>(true, null, null);
	}

	public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
		return new ApiResponse<>(false, null, new ApiError(errorCode.getMessage(), errorCode.name()));
	}
}
