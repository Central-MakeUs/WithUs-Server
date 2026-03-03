package com.herethere.withus.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null) {
			try {
				JwtPayload payload = jwtUtil.validateAccessToken(token);

				Authentication authentication = new UsernamePasswordAuthenticationToken(payload.userId(), null,
					List.of()
				);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (JwtValidationException e) {
				SecurityContextHolder.clearContext();
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
}
