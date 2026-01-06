package com.herethere.withus.common.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.common.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		// 인증되지 않은 유저가 접근했을 때 호출됨 (401 Unauthorized)
		setErrorResponse(response);
	}

	private void setErrorResponse(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		ApiResponse<Void> apiResponse = ApiResponse.failure(ErrorCode.EMPTY_JWT_TOKEN);
		String result = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(result);
	}
}
