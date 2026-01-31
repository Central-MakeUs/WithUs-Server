package com.herethere.withus.archive.api;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionDetailResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionListResponse;
import com.herethere.withus.archive.enums.ArchiveType;
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
			- ArchiveInfo의 imageUrl이 둘 다 null인 경우는 존재하지 않고, 하나만 null인 경우에는 프론트에서 해당 사진만 보여줍니다.
			- size는 칸의 개수가 아니라 날짜의 개수를 의미합니다. size가 20이면, 20일 치의 이미지를 가져옵니다.
			- response에서 ImageInfo의 id만으로 식별하지 않고, archiveType과 id를 합쳐서 해당 사진을 식별합니다.
			""")
	@GetMapping("/me/couple/archives")
	ResponseEntity<ApiResponse<ArchiveListResponse>> getArchivesByCursor(
		@Parameter(
			description = "한 번에 조회할 네컷 사진 개수 (기본값: 20, 최소 1, 최대 50)",
			example = "20"
		)
		@RequestParam(defaultValue = "20")
		@Min(1)
		@Max(30)
		int size,

		@Parameter(
			description = "다음 페이지 조회를 위한 커서 값 (첫 페이지 조회 시 생략)",
			example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTIzVDAxOjExOjUyIiwiaWQiOjExOX0="
		)
		@RequestParam(required = false)
		String cursor
	);

	@Operation(
		summary = "보관 사진 날짜 기준 조회",
		description = """
			우리 커플의 보관 사진을 특정 날짜 기준으로 전체 조회합니다.
			- 날짜는 YYYY-MM-DD 형식입니다.
			- 해당 날짜에 촬영된 모든 보관 사진을 반환합니다.
			- `/me/couple/archives` 응답의 date, 엔티티 id, ArchiveType으로 요청을 합니다.
			- 사진을 보내지 않았다면 해당 사람 imageInfo의 answerImageUrl은 null입니다.
			- archiveType과 id를 합쳐서 사용자가 어떤 사진을 선택했는지를 식별하고, 해당 사진을 selected = true로 응답합니다.
			- 리스트 중에 selected: true인 항목이 있으면 해당 위치로 스크롤되어 있는 상태로 유저에게 보여줘야 합니다.
			- 만약 둘 중 하나라도 보내지 않았거나, selected 된 사진이 없다면, 모든 리스트의 selected = false가 되고, 그 땐 제일 앞의 항목을 보여줘야 합니다.
			- selected = true는 하나 뿐이거나, 0개 입니다(선택 되지 않았을 때).
			"""
	)
	@GetMapping("/me/couple/archives/date")
	ResponseEntity<ApiResponse<ArchiveDateResponse>> getArchiveByDate(
		@Parameter(
			description = "조회할 날짜 (YYYY-MM-DD)",
			example = "2026-01-23",
			required = true
		)
		@RequestParam
		LocalDate date,

		@Parameter(description = "클릭한 사진의 고유 ID", example = "101")
		@RequestParam(required = false) Long targetId,

		@Parameter(description = "클릭한 사진의 타입 (QUESTION, KEYWORD)", example = "QUESTION")
		@RequestParam(required = false) ArchiveType targetType
	);

	@Operation(
		summary = "보관 질문 조회",
		description = """
			우리 커플의 보관 질문을 전체 조회합니다.
			- 커서 기반 페이지네이션을 사용합니다.
			- 응답에서 받은 nextCursor 값을 그대로 cursor에 넣어 요청하면 됩니다.
			- 커서는 받은 질문의 number를 기준으로 생성됩니다.
			"""
	)
	@GetMapping("/me/couple/archives/questions")
	ResponseEntity<ApiResponse<ArchiveQuestionListResponse>> getArchiveQuestions(
		@Parameter(
			description = "한 번에 조회할 질문 개수 (기본값: 20, 최소 1, 최대 50)",
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
		summary = "보관 질문 상세 조회",
		description = """
			우리 커플의 보관 질문을 상세 조회합니다.
			- `/api/me/couple/archives/questions`의 응답의 id를 path에 넣어서 요청합니다.
			- 둘 모두의 사진이 존재하지 않는 경우가 있을 수 있습니다. (추후 사진 삭제 시)
			- 둘 모두의 사진이 없어 빈 리스트일 경우엔, 삭제된 사진이라는 메시지를 띄워줘야 합니다.
			"""
	)
	@GetMapping("/me/couple/archives/questions/{coupleQuestionId}")
	ResponseEntity<ApiResponse<ArchiveQuestionDetailResponse>> getDetailArchiveQuestion(
		@Parameter(
			description = "couple-question id",
			example = "11"
		)
		@PathVariable
		Long coupleQuestionId
	);
}
