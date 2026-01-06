package com.herethere.withus.common.jwt;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.herethere.withus.common.exception.JwtValidationException;
import com.herethere.withus.common.jwt.dto.JwtPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private static final long ACCESS_TOKEN_EXPIRE = 1000L * 60 * 60 * 12; // 12시간
	private static final String CLAIM_NICKNAME = "nickname";
	private final String secretKey;
	private final Key key;

	public JwtUtil(@Value("${jwt.secret}") String secretKey) {
		this.secretKey = secretKey;
		key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String createToken(JwtPayload payload) {
		Claims claims = Jwts.claims().setSubject(String.valueOf(payload.userId()));
		claims.put(CLAIM_NICKNAME, payload.nickname());

		Date now = new Date();
		Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public JwtPayload validateToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
			return new JwtPayload(Long.valueOf(claims.getSubject()), claims.get(CLAIM_NICKNAME).toString());
		} catch (ExpiredJwtException e) {
			throw new JwtValidationException(EXPIRED_JWT_TOKEN);
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtValidationException(INVALID_JWT_TOKEN);
		}
	}
}
