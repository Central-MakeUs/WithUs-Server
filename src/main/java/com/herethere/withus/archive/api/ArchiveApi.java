package com.herethere.withus.archive.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.common.apiresponse.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RequestMapping("/api")
@Tag(name = "보관 API", description = "보관 탭 API")
public interface ArchiveApi {

	@Operation(
		summary = "보관 사진 최신순 조회",
		description = """
			커플의 보관 사진을 최신순으로 조회합니다.
			- 커서 기반 페이지네이션을 사용합니다.
			- 응답에서 받은 nextCursor 값을 그대로 cursor에 넣어 요청하면 됩니다.
			- 커서는 date를 기반으로 생성됩니다.
			- 상세 정보를 요청할 때는, date를 가지고 요청하면 됩니다.
			- 둘 다 null인 경우는 존재하지 않고, 하나만 null인 경우에는 프론트에서 해당 사진만 보여줍니다.
			""")
	@GetMapping("/me/couple/archives")
	ResponseEntity<ApiResponse<ArchiveListResponse>> getArchivesByCursor(
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
}
