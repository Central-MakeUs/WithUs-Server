package com.herethere.withus.question.api;

import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/questions")
@Tag(name = "오늘의 질문 API")
public interface QuestionApi {
}
