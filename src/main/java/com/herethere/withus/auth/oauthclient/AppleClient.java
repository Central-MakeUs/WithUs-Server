package com.herethere.withus.auth.oauthclient;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.AppleTokenRequest;
import com.herethere.withus.auth.dto.response.ApplePublicKeyResponse;
import com.herethere.withus.auth.dto.response.AppleTokenResponse;
import com.herethere.withus.auth.externalapi.AppleApiClient;
import com.herethere.withus.auth.repository.AppleRefreshTokenRepository;
import com.herethere.withus.common.exception.AuthException;
import com.herethere.withus.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service("apple")
@RequiredArgsConstructor
public class AppleClient implements OAuthClient {

	private static final String APPLE_ISSUER = "https://appleid.apple.com";
	private final AppleApiClient appleApiClient;
	private final ApplePublicKeyGenerator applePublicKeyGenerator;
	private final AppleKeyGenerator appleKeyGenerator;
	private final AppleRefreshTokenRepository appleRefreshTokenRepository;
	private final ObjectMapper objectMapper;
	@Value("${oauth.apple.app.id}")
	private String clientId;

	@Override
	public OAuthUserInfo getUserInfo(String oauthToken, String authorizationCode) {
		// 토큰 검증 및 정보 jwt에서 정보 추출
		Claims claims = verifyIdentityToken(oauthToken);
		String appleUserId = claims.getSubject();
		String refreshToken = getAppleRefreshToken(authorizationCode);
		return new OAuthUserInfo(appleUserId, refreshToken);
	}

	@Override
	public void withdrawUser(User user) {

	}

	private String getAppleRefreshToken(String authorizationCode) {
		String clientSecret = appleKeyGenerator.getClientSecret();
		AppleTokenResponse response = appleApiClient.findAppleToken(
			new AppleTokenRequest(clientId, clientSecret, authorizationCode, "authorization_code"));
		return response.refreshToken();
	}

	private Claims verifyIdentityToken(String identityToken) {
		try {
			ApplePublicKeyResponse response = appleApiClient.findAppleAuthPublicKeys();

			String identityTokenHeader = identityToken.substring(0, identityToken.indexOf("."));
			String decodedIdentityTokenHeader = new String(Base64.getUrlDecoder().decode(identityTokenHeader),
				StandardCharsets.UTF_8);

			Map<String, String> identityTokenHeaderMap = objectMapper.readValue(decodedIdentityTokenHeader, Map.class);

			PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(identityTokenHeaderMap, response);

			Claims claims = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(identityToken)
				.getBody();

			if (!APPLE_ISSUER.equals(claims.getIssuer())) {
				throw new AuthException(APPLE_TOKEN_VALIDATION_ERROR);
			}

			if (!clientId.equals(claims.getAudience())) {
				throw new AuthException(APPLE_TOKEN_VALIDATION_ERROR);
			}

			if (claims.getSubject() == null) {
				throw new AuthException(APPLE_TOKEN_VALIDATION_ERROR);
			}

			return claims;
		} catch (ExpiredJwtException e) {
			throw new AuthException(EXPIRED_JWT_TOKEN);
		} catch (Exception e) {
			throw new AuthException(APPLE_TOKEN_VALIDATION_ERROR);
		}
	}
}
