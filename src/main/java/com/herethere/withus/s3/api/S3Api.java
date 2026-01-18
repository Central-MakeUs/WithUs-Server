package com.herethere.withus.s3.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/images")
@Tag(name = "이미지 업로드 API", description = "S3 업로드를 위한 URL 생성 (Presigned URL)")
public interface S3Api {

	@Operation(summary = "Presigned URL 발급", description = """
		S3에 사진을 직접 업로드하기 위한 임시 URL을 발급합니다.
		**[업로드 프로세스]**
		1. 본 API를 호출하여 `uploadUrl`,`accessUrl` `imageKey`를 받습니다.
		2. 응답받은 `uploadUrl`로 PUT 요청을 보냅니다. 이미지 형식은 반드시 .jpg여야 합니다. (해당 url은 60분간 유효)
		3. 업로드가 완료되면 응답받은 'accessUrl'을 통해 업로드한 이미지를 조회할 수 있습니다. (해당 url은 60분간 유효)
		4. 업로드가 완료되면 응답받은 `imageKey`를 각 도메인 API(질문 응답, 프로필 설정 등)의 Request Body에 담아 보냅니다.
		
		**[제약 사항]**
		- 이미지 형식: 반드시 .jpg
		- 업로드 용도(imageType): PROFILE(유저 프로필용), MEMORY(질문/키워드 인증용)
		""")
	@PostMapping("/presigned-url")
	ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@Valid @RequestBody PresignedUrlRequest request);
}
