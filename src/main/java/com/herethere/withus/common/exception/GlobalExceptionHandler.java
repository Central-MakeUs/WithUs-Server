package com.herethere.withus.common.exception;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.herethere.withus.common.apiresponse.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
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

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
		String errorMessage = ex.getConstraintViolations()
			.stream()
			.findFirst()
			.map(ConstraintViolation::getMessage)
			.orElse("잘못된 요청입니다.");

		log.warn("RequestParam / PathVariable 검증 실패: {}", errorMessage);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.failure(errorMessage, INVALID_INPUT.name()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String message = String.format(
			"요청 파라미터 '%s' 값 '%s'를(을) %s 타입으로 변환할 수 없습니다.",
			ex.getName(),
			ex.getValue(),
			ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "올바른");
		log.warn("타입 변환 실패: {}", ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.failure(message, INVALID_INPUT.name()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleJsonParse(HttpMessageNotReadableException ex) {
		log.warn("JSON 파싱 실패: {}", ex.getMessage());

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.failure("요청 본문(JSON)을 읽을 수 없습니다.", INVALID_INPUT.name()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
		log.error("내부 서버 에러: ", ex);
		return ResponseEntity
			.status(INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiResponse.failure(INTERNAL_SERVER_ERROR));
	}
}
