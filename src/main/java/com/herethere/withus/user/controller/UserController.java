package com.herethere.withus.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.dto.ApiResponse;
import com.herethere.withus.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<Void>> getUsers() {
		return ResponseEntity.ok(ApiResponse.success());
	}
}
