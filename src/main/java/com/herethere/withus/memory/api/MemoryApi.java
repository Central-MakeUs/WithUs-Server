package com.herethere.withus.memory.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.memory.domain.MemoryType;
import com.herethere.withus.memory.dto.request.CustomMemoryCreateRequest;
import com.herethere.withus.memory.dto.request.MemoryCreateRequest;
import com.herethere.withus.memory.dto.response.MemoryDetailResponse;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@RequestMapping("/api")
@Tag(name = "추억 API")
public interface MemoryApi {

	@Operation(
		summary = "월별 주 메모리 조회",
		description = """
			선택한 월에 해당하는 주별 메모리를 전체 조회합니다. - 추억 메인 페이지에서 사용
			- 한 주는 일요일 ~ 토요일 기준입니다.
			- weekEndDate(토요일)가 해당 월에 속하면 해당 월의 주로 판단합니다.
			- WeekMemorySummary의 status로 보여줄 화면을 선택합니다. status는 UNAVAILABLE, NEED_CREATE, CREATED입니다.
			if memoryType == CUSTOM:
			  - customMemoryId != null
			  - status == CREATED
			  - createdImageUrl 사용
			if memoryType == WEEK:
			  - weekEndDate != null
			  - status에 따라 분기
			- status 가 UNAVAILABLE일 경우, 두 명 모두 6장 이상(...) 이라는 문구를 띄웁니다. (사진 부족 시)
			- status 가 NEED_CREATE일 경우, needCreateImageUrls를 통해 화면을 만들고, 해당 이미지를 업로드하는 api를 바로 사용합니다. (아직 사진 생성하지 않았을 시)
			- status 가 CREATE일 경우, 이미 만들어진 사진이기 때문에 createImageUrl을 사용하여 사진을 띄웁니다. (이미 사진 생성 했을 시)
			"""
	)
	@GetMapping("/me/couple/memories")
	ResponseEntity<ApiResponse<MonthMemoryResponse>> getMonthMemories(
		@Parameter(
			description = "조회할 월 (YYYYMM 형식)",
			example = "202602",
			required = true
		)
		@RequestParam
		int monthKey
	);

	@Operation(
		summary = "week 추억 생성 (자동 생성)",
		description = """
			프론트에서 합성한 추억 이미지를 서버에 저장합니다. - 커스텀 생성이 아닌, 자동 생성 API
			- `GET /api/me/couple/memories/` 응답의 status가 `NEED_CREATE` 인 사진 클릭 시 이 API를 호출합니다.
			- 응답의 weekEndDate를 통해 해당 사진을 식별합니다.
			- weekEndDate는 토요일이어야만 합니다.
			- 이미 추억이 존재하는 주(weekEndDate)에 대해 요청하면 실패합니다.
			- imageKey는 presigned-url 업로드 후 받은 최종 imageKey여야 합니다.
			"""
	)
	@PostMapping("/me/couple/memories/{weekEndDate}")
	ResponseEntity<ApiResponse<Void>> createMemory(
		@Parameter(
			description = "주 종료일 (토요일, ISO-8601 형식: YYYY-MM-DD)",
			example = "2026-02-14"
		)
		@PathVariable
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate weekEndDate,

		@Valid @RequestBody
		MemoryCreateRequest request
	);

	@Operation(
		summary = "커스텀 추억 생성",
		description = """
			커스텀 추억 사진을 업로드 합니다.
			- /api/images/presigned-url에서 response로 받은 ImageKey를 보내야 합니다.
			"""
	)
	@PostMapping("/me/couple/memories")
	ResponseEntity<ApiResponse<Void>> createCustomMemory(
		@Valid @RequestBody CustomMemoryCreateRequest request
	);

	@Operation(
		summary = "추억 상세 조회",
		description = """
				커플의 추억 상세 정보를 조회합니다.
				- memoryType의 종류에 따라 사용하는 식별자가 다릅니다. (memoryType은 필수입니다.)
				- memoryType이 WEEK_MEMORY일 경우, weekEndDate가 식별자가 됩니다.
				- memoryType이 CUSTOM_MEMORY일 경우, customMemoryId가 식별자가 됩니다.
			"""
	)
	@GetMapping("/me/couple/memories/detail")
	ResponseEntity<ApiResponse<MemoryDetailResponse>> getMemoryDetail(
		@Parameter(description = "클릭한 사진의 타입 (WEEK_MEMORY, CUSTOM_MEMORY)", example = "WEEK_MEMORY")
		@RequestParam
		MemoryType memoryType,

		@Parameter(
			description = "WEEK_MEMORY일 때 사용하는, weekEndDate",
			example = "2026-01-23"
		)
		@RequestParam(required = false)
		LocalDate weekEndDate,

		@Parameter(description = "CUSTOM_MEMORY일 때 사용하는, 사진의 고유 ID", example = "101")
		@RequestParam(required = false)
		Long targetId
	);
}
