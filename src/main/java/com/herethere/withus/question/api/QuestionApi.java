package com.herethere.withus.question.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/me")
@Tag(name = "오늘의 질문 API")
public interface QuestionApi {
	@Operation(summary = "우리 커플의 오늘 질문 조회")
	@GetMapping("/couple/question/today")
	ResponseEntity<ApiResponse<TodayQuestionResponse>> getTodayQuestion();

	@Operation(summary = "커플의 오늘 질문에 대한 사진 전송")
	@PostMapping("/couple/question/today/image")
	ResponseEntity<ApiResponse<Void>> uploadTodayQuestionImage(@Valid @RequestBody TodayQuestionImageRequest request);
}
