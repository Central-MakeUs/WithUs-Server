package com.herethere.withus.archive.api;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.herethere.withus.archive.dto.request.ArchiveBulkDeleteRequest;
import com.herethere.withus.archive.dto.request.ArchiveDeleteRequest;
import com.herethere.withus.archive.dto.response.ArchiveCalendarResponse;
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

@Validated
@RequestMapping("/api")
@Tag(name = "보관 API", description = "보관 탭 API")
public interface ArchiveApi {

	@Operation(
		summary = "보관 사진 최신순 조회",
		description = """
			커플의 보관 사진을 최신순으로 조회합니다. - 최신순 목록 조회에 사용합니다.
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
			최신순 목록 조회에서 특정 사진을 클릭했을 때나, 캘린더에서 특정 날짜를 클릭했을 때, 상세 조회시 사용합니다.
			- 날짜는 YYYY-MM-DD 형식입니다.
			- 해당 날짜에 촬영된 모든 보관 사진을 반환합니다.
			- `/me/couple/archives` 응답의 date, 엔티티 id, ArchiveType으로 요청을 합니다.
			- 사진을 보내지 않았다면 해당 사람 imageInfo의 answerImageUrl은 null입니다.
			- archiveType과 id를 합쳐서 사용자가 어떤 사진을 선택했는지를 식별하고, 해당 사진을 selected = true로 응답합니다.
			- 리스트 중에 selected: true인 항목이 있으면 해당 위치로 스크롤되어 있는 상태로 유저에게 보여줘야 합니다.
			- 만약 archiveType과 id 중 하나라도 보내지 않았거나, selected 된 사진이 없다면, 모든 리스트의 selected = false가 되고, 그 땐 제일 앞의 항목을 보여줘야 합니다.
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
			우리 커플의 보관 질문을 전체 조회합니다. - 질문 목록 조회에 사용합니다.
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
			우리 커플의 보관 질문을 상세 조회합니다. - 질문 목록 조회에서 상세 조회할 때 사용합니다.
			- `/api/me/couple/archives/questions`의 응답의 id를 path에 넣어서 요청합니다.
			- 둘 모두의 사진이 존재하지 않는 경우가 있을 수 있습니다. (추후 사진 삭제 시)
			- 둘 모두의 사진이 없을 경우엔, 삭제된 사진이라는 메시지를 띄워줘야 합니다.
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

	@Operation(
		summary = "보관 사진 단일 삭제",
		description = """
			보관함에서 특정 항목의 사진을 모두 삭제합니다.
			- archiveType, id, date를 함께 전달하여 삭제할 항목을 식별합니다.
			- archiveType은 QUESTION 또는 KEYWORD 중 하나입니다.
			- id는 해당 archiveType 엔티티의 고유 ID입니다. (`/me/couple/archives` 응답의 ImageInfo.id)
			- date는 해당 항목의 날짜입니다. (`/me/couple/archives` 응답의 ArchiveInfo.date)
			- 나와 상대방 모두의 이미지가 삭제됩니다.
			- 해당 항목에 사진이 없거나, 접근 권한이 없을 경우 에러가 반환됩니다.
			"""
	)
	@DeleteMapping("/me/couple/archives")
	ResponseEntity<ApiResponse<Void>> deleteArchive(
		@RequestBody ArchiveDeleteRequest request
	);

	@Operation(
		summary = "보관 사진 복수 삭제",
		description = """
			보관함에서 여러 항목의 사진을 한 번에 삭제합니다.
			- items 리스트에 삭제할 항목들을 담아 전달합니다.
			- 각 항목은 archiveType, id, date로 식별합니다.
			- 리스트 중 하나라도 오늘 날짜이거나 접근 권한이 없을 경우 전체 요청이 실패합니다.
			- 나와 상대방 모두의 이미지가 삭제됩니다.
			"""
	)
	@DeleteMapping("/me/couple/archives/bulk")
	ResponseEntity<ApiResponse<Void>> bulkDeleteArchive(
		@RequestBody ArchiveBulkDeleteRequest request
	);

	@Operation(
		summary = "보관 캘린더 월 단위 조회",
		description = """
		우리 커플의 보관 사진을 월 단위 캘린더 형태로 조회합니다. - 캘린더 섬네일 목록 조회시 사용합니다.
		- year, month 기준으로 해당 월의 사진이 있는 날짜를 반환합니다.
		- 보관 데이터가 없는 날짜는 포함되지 않습니다. (모든 날짜가 있는 게 더 편하면 수정 가능합니다.)
		- 각 날짜마다 나(me) / 상대방(partner)의 업로드 사진 URL 을 제공합니다.
		- 질문 사진 + 키워드 사진을 통합한 기준입니다.
		- 대표 사진 우선순위는 질문 사진 > 키워드 사진입니다.
		"""
	)
	@GetMapping("/me/couple/archives/calendar")
	ResponseEntity<ApiResponse<ArchiveCalendarResponse>> getArchiveCalendar(
		@Parameter(
			description = "조회할 연도",
			example = "2026",
			required = true
		)
		@RequestParam
		@Min(2000)
		@Max(2100)
		int year,

		@Parameter(
			description = "조회할 월 (1 ~ 12)",
			example = "1",
			required = true
		)
		@RequestParam
		@Min(1)
		@Max(12)
		int month
	);

}
