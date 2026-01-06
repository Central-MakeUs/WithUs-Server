package com.herethere.withus.auth.externalapi;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.herethere.withus.common.exception.InvalidOAuthTokenException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorHandler implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {

		if (response.status() == 401) {
			return new InvalidOAuthTokenException(OAUTH_TOKEN_ERROR);
		}

		return new RuntimeException("Feign Error : " + response.status());
	}
}
