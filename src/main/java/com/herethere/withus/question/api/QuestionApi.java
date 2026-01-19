package com.herethere.withus.question.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/me")
@Tag(name = "오늘의 질문 API", description = "매일 정해진 시간에 배달되는 커플 공통 질문 관리")
public interface QuestionApi {

	@Operation(
		summary = "오늘의 질문 상세 조회",
		description = """
			오늘 우리 커플에게 배정된 질문과 답변을 조회합니다.
			- 별도의 ID 파라미터 없이, 현재 시간과 커플 정보를 기준으로 서버가 질문을 찾아 반환합니다. (가장 최신의 것을 반환)
			- 응답의 myInfo / partnerInfo를 통해 사진을 확인할 수 있습니다.
			- 아직 첫 질문이 생성되지 않았으면 coupleQuestionId 가 null이고, question에 몇 시간 후 질문이 생성되는지 보여줍니다. 
			- 사진을 올리지 않으면 Info의 questionImageUrl과 answeredAt이 null입니다.
			- Info의 profileImageUrl이 null이면 앱의 기본이미지로 대체합니다.
			- 응답 바디에 포함된 coupleQuestionId를 사용하여 `POST /couple/questions/{coupleQuestionId}/image`로 사진을 업로드합니다.
			- partnerInfo의 userId를 사용하여 콕찌르기 버튼을 만듭니다.
			"""
	)
	@GetMapping("/couple/question/today")
	ResponseEntity<ApiResponse<TodayQuestionResponse>> getTodayQuestion();

	@Operation(
		summary = "오늘의 질문 사진 업로드",
		description = """
			오늘의 질문에 대한 사진 답변을 등록합니다.
			- coupleQuestionId: `GET /couple/question/today` 응답에서 받은 ID를 사용합니다.
			- `GET /api/images/presigned-url`에서 response로 받은 ImageKey를 보내야 합니다.
			- 이미 사진을 업로드한 경우 실패합니다.
			"""
	)
	@PostMapping("/couple/questions/{coupleQuestionId}/image")
	ResponseEntity<ApiResponse<Void>> uploadTodayQuestionImage(
		@Parameter(description = "오늘의 질문-커플 매핑 고유 ID", example = "505")
		@PathVariable Long coupleQuestionId,
		@Valid @RequestBody TodayQuestionImageRequest request
	);
}
