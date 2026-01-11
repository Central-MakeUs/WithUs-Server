package com.herethere.withus.s3.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/images")
@Tag(name = "이미지 업로드 API")
public interface S3Api {

	@Operation(summary = "Presigned URL 발급 API", description = "이미지는 무조건 .jpg 형식으로 보내야합니다.")
	@GetMapping("/presigned-url")
	ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@Valid @RequestBody PresignedUrlRequest request);
}
