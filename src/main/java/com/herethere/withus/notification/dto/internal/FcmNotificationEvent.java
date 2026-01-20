package com.herethere.withus.notification.dto.internal;

import java.util.Map;

import com.herethere.withus.user.domain.User;

public record FcmNotificationEvent(Long userId, String title, String body, Map<String, String> data) {
	public static FcmNotificationEvent of(Long userId, String title, String body, Map<String, String> data) {
		return new FcmNotificationEvent(
			userId,
			title,
			body,
			data != null ? data : Map.of()
		);
	}

	public static FcmNotificationEvent createPokeEvent(User sender, User receiver) {
		return FcmNotificationEvent.of(
			receiver.getId(),
			sender.getNickname() + "님이 사진을 기다리고 있어요!",
			"지금 바로 사진을 보내볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}

	public static FcmNotificationEvent createUploadEvent(User sender, User receiver) {
		return FcmNotificationEvent.of(
			receiver.getId(),
			sender.getNickname() + "님이 사진을 보냈어요!",
			"사진이 도착했어요, 확인해볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}

	public static FcmNotificationEvent createNewQuestionEvent(User receiver) {
		return FcmNotificationEvent.of(
			receiver.getId(),
			"오늘의 랜덤 질문이 도착했어요",
			"오늘의 질문에 답해볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}
}
