package com.herethere.withus.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.notification.api.NotificationApi;
import com.herethere.withus.notification.dto.response.NotificationCursorResponse;
import com.herethere.withus.notification.service.NotificationHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

	private final NotificationHistoryService notificationHistoryService;

	@Override
	public ResponseEntity<ApiResponse<NotificationCursorResponse>> getNotifications(int size, String cursor) {
		NotificationCursorResponse response = notificationHistoryService.getNotificationHistory(size, cursor);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
