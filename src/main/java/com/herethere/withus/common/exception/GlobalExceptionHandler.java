package com.herethere.withus.common.exception;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.herethere.withus.common.apiresponse.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(BaseException ex) {
		log.warn("비즈니스 예외 발생: {}", ex.getErrorCode().getMessage());
		return ResponseEntity
			.status(ex.getErrorCode().getStatus())
			.body(ApiResponse.failure(ex.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(error -> error.getDefaultMessage())
			.orElse("잘못된 요청입니다.");

		log.warn("유효성 검사 실패 예외 발생: {}", errorMessage);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.failure(errorMessage, INVALID_INPUT.name()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
		log.error("내부 서버 에러: ", ex);
		return ResponseEntity
			.status(INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiResponse.failure(INTERNAL_SERVER_ERROR));
	}
}
