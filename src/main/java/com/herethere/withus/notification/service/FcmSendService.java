package com.herethere.withus.notification.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.herethere.withus.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmSendService {
	private final FirebaseMessaging firebaseMessaging;
	private final FcmTokenManager fcmTokenManager;

	public void sendToUser(User user, String title, String body, Map<String, String> data) {
		List<String> tokens = fcmTokenManager.getTokensByUser(user);

		if (tokens.isEmpty()) {
			log.info("FCM 토큰 없음. userId={}", user.getId());
			return;
		}

		for (String token : tokens) {
			sendToToken(token, title, body, data);
		}
	}

	public void sendToToken(String token, String title, String body, Map<String, String> data) {
		Map<String, String> safeData = data == null ? Map.of() : data;

		Message message = Message.builder()
			.setToken(token)
			.setNotification(
				Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
			.putAllData(safeData)
			.build();

		try {
			String response = firebaseMessaging.send(message);
			log.debug("FCM 전송 성공: {}", response);

		} catch (FirebaseMessagingException e) {
			handleSendFailure(token, e);
		}
	}

	private void handleSendFailure(String token, FirebaseMessagingException e) {
		log.warn("FCM 전송 실패 token={}, errorCode={}", token, e.getErrorCode(), e);

		// 대표적인 만료 토큰 에러
		if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
			|| e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT
			|| e.getMessagingErrorCode() == MessagingErrorCode.SENDER_ID_MISMATCH) {

			log.info("만료된 토큰 제거: {}", token);
			fcmTokenManager.deleteFcmTokenByTokenValue(token);
		}
	}
}
