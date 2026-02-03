package com.herethere.withus.auth.oauthclient;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.response.ApplePublicKeyResponse;
import com.herethere.withus.auth.externalapi.AppleApiClient;
import com.herethere.withus.common.exception.AuthException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service("apple")
@RequiredArgsConstructor
public class AppleClient implements OAuthClient {

	private final AppleApiClient appleApiClient;
	private final ObjectMapper objectMapper;

	@Override
	public OAuthUserInfo getUserInfo(String oauthToken, String authorizationCode) {
		// 토큰 검증 및 정보 jwt에서 정보 추출
		Claims claims = verifyIdentityToken(oauthToken);
		String appleUserId = claims.getSubject();
		// 여기에 authorizationCode 를 사용하여 refresh token 가져오고, db에 저장하는 로직 추가 필요

		return new OAuthUserInfo(appleUserId);
	}

	private Claims verifyIdentityToken(String identityToken) {
		try {
			ApplePublicKeyResponse response = appleApiClient.findAppleAuthPublicKeys();

			String identityTokenHeader = identityToken.substring(0, identityToken.indexOf("."));

			//identityTokenHeader decode
			String decodedIdentityTokenHeader = new String(Base64.getUrlDecoder().decode(identityTokenHeader),
				StandardCharsets.UTF_8);

			Map<String, String> identityTokenHeaderMap = objectMapper.readValue(decodedIdentityTokenHeader, Map.class);
			ApplePublicKeyResponse.AppleKeyInfo appleKeyInfo = response.keys().stream()
				.filter(key -> Objects.equals(key.kid(), identityTokenHeaderMap.get("kid"))
					&& Objects.equals(key.alg(), identityTokenHeaderMap.get("alg")))
				.findFirst()
				.orElseThrow(() -> new AuthException(APPLE_PUBLIC_KEY_ERROR));

			byte[] nBytes = Base64.getUrlDecoder().decode(appleKeyInfo.n());
			byte[] eBytes = Base64.getUrlDecoder().decode(appleKeyInfo.e());

			BigInteger n = new BigInteger(1, nBytes);
			BigInteger e = new BigInteger(1, eBytes);

			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
			KeyFactory keyFactory = KeyFactory.getInstance(appleKeyInfo.kty());
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			return Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(identityToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new AuthException(EXPIRED_JWT_TOKEN);
		} catch (Exception e) {
			throw new AuthException(APPLE_TOKEN_VALIDATION_ERROR);
		}
	}
}
