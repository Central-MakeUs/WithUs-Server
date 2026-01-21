package com.herethere.withus.user.dto.response;

import java.util.List;
import java.util.Set;

import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.user.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserOnboardingResponse(
	@Schema(description = "유저 고유 ID", example = "1")
	Long userId,

	@Schema(description = "닉네임", example = "하늘이")
	String nickname,

	@Schema(description = "유저 키워드 정보 리스트")
	List<KeywordInfo> keywordInfoList,

	@Schema(
		description = "프로필 이미지 URL",
		example = "https://s3.com/profiles/1/uuid_image.jpg",
		nullable = true
	)
	String profileImageUrl
) {
	public static UserOnboardingResponse from(User user, Set<Keyword> keywordSet) {
		List<KeywordInfo> keywordInfos = keywordSet.stream().map(
			k -> new KeywordInfo(k.getId(), k.getContent())
		).toList();

		return new UserOnboardingResponse(user.getId(), user.getNickname(), keywordInfos, user.getProfileImageKey());
	}

	public record KeywordInfo(
		@Schema(description = "키워드 고유 ID", example = "1")
		Long keywordId,

		@Schema(description = "키워드 내용", example = "밥타임")
		String content
	) {
	}
}

