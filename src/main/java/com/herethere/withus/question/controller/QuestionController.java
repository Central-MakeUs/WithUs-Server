package com.herethere.withus.question.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.question.api.QuestionApi;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;
import com.herethere.withus.question.service.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuestionController implements QuestionApi {
	private final QuestionService questionService;

	@Override
	public ResponseEntity<ApiResponse<TodayQuestionResponse>> getTodayQuestion() {
		return null;
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> uploadTodayQuestionImage(TodayQuestionImageRequest request) {
		questionService.uploadTodayQuestionImage(request);
		return null;
	}
}
