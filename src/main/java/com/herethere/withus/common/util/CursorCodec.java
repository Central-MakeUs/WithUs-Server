package com.herethere.withus.common.util;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herethere.withus.common.dto.internal.CursorPayload;
import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.InternalServerException;

@Component
public class CursorCodec {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public CursorPayload decode(String cursor) {
		try {
			byte[] decoded = Base64.getUrlDecoder().decode(cursor);
			String json = new String(decoded, StandardCharsets.UTF_8);
			return objectMapper.readValue(json, CursorPayload.class);
		} catch (Exception e) {
			throw new BadRequestException(INVALID_CURSOR);
		}
	}

	public String encode(LocalDateTime createdAt, Long id) {
		try {
			CursorPayload payload = new CursorPayload(createdAt, id);
			String json = objectMapper.writeValueAsString(payload);
			return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new InternalServerException(CURSOR_ENCODING_FAILED);
		}
	}

}
