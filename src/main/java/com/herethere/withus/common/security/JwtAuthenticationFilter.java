package com.herethere.withus.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.common.exception.JwtValidationException;
import com.herethere.withus.common.jwt.JwtUtil;
import com.herethere.withus.common.jwt.dto.JwtPayload;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null) {
			try {
				JwtPayload payload = jwtUtil.validateToken(token);

				Authentication authentication = new UsernamePasswordAuthenticationToken(payload.userId(), null,
					List.of() // 권한 추가 가능
				);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (JwtValidationException e) {
				sendErrorResponse(response, e);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");

		if (bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}

		return null;
	}

	private void sendErrorResponse(HttpServletResponse response, JwtValidationException e) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		ApiResponse<Void> apiResponse = ApiResponse.failure(e.getErrorCode());
		String result = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(result);
	}
}
