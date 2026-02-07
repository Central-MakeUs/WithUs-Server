package com.herethere.withus.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoRevokeRequest {
	String target_id_type;
	Long target_id;
}
