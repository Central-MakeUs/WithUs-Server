package com.herethere.withus.auth.oauthclient;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.herethere.withus.common.exception.BaseException;
import com.herethere.withus.common.exception.ErrorCode;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppleKeyGenerator {
	private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

	@Value("${oauth.apple.app.keyId}")
	private String kid;
	@Value("${oauth.apple.app.teamId}")
	private String teamId;
	@Value("${oauth.apple.app.id}")
	private String appId;
	@Value("${oauth.apple.app.privateKey}")
	private String privateKey;

	public String getClientSecret() {
		Date expirationDate = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.of("UTC")).toInstant());

		return Jwts.builder()
			.setHeaderParam("kid", kid)
			.setHeaderParam("alg", "ES256")
			.setIssuer(teamId)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(expirationDate)
			.setAudience(APPLE_AUDIENCE)
			.setSubject(appId)
			.signWith(getPrivateKey(), SignatureAlgorithm.ES256)
			.compact();
	}

	private PrivateKey getPrivateKey() {
		try {
			Reader pemReader = new StringReader(privateKey.replace("\\n", "\n"));
			PEMParser pemParser = new PEMParser(pemReader);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			PrivateKeyInfo object = (PrivateKeyInfo)pemParser.readObject();
			return converter.getPrivateKey(object);
		} catch (IOException e) {
			throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
