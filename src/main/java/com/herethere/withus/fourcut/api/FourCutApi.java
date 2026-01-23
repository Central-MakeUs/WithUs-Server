package com.herethere.withus.fourcut.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.fourcut.dto.request.FourCutUploadRequest;
import com.herethere.withus.fourcut.dto.response.FourCutCursorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RequestMapping("/api")
@Tag(name = "네컷 API", description = "네컷 사진 관리")
public interface FourCutApi {

	@Operation(
		summary = "네컷 사진 조회",
		description = """
			우리 커플의 네컷 사진을 조회합니다.
			- 커서 기반 페이지네이션을 사용합니다.
			- 응답에서 받은 nextCursor 값을 그대로 cursor에 넣어 요청하면 됩니다.
			- 커서는 createdAt과 id를 기반으로 생성됩니다.
			"""
	)
	@GetMapping("/me/couple/four-cuts")
	ResponseEntity<ApiResponse<FourCutCursorResponse>> getFourCutsByCursor(
		@Parameter(
			description = "한 번에 조회할 네컷 사진 개수 (기본값: 20, 최소 1, 최대 50)",
			example = "20"
		)
		@RequestParam(defaultValue = "20")
		@Min(1)
		@Max(50)
		int size,

		@Parameter(
			description = "다음 페이지 조회를 위한 커서 값 (첫 페이지 조회 시 생략)",
			example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTIzVDAxOjExOjUyIiwiaWQiOjExOX0="
		)
		@RequestParam(required = false)
		String cursor
	);

	@Operation(
		summary = "네컷 사진 업로드",
		description = """
			네컷 사진을 업로드 합니다.
			- /api/images/presigned-url에서 response로 받은 ImageKey를 보내야 합니다.
			"""
	)
	@PostMapping("/me/couple/four-cuts")
	ResponseEntity<ApiResponse<Void>> uploadFourCutImage(
		@Valid @RequestBody FourCutUploadRequest fourCutUploadRequest
	);
}
