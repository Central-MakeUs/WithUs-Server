package com.herethere.withus.question.controller;

import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.question.api.QuestionApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuestionController implements QuestionApi {
}
