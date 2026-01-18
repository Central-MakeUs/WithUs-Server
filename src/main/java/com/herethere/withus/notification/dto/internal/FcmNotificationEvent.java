package com.herethere.withus.notification.dto.internal;

import java.util.Map;

public record FcmNotificationEvent(Long userId, String title, String body, Map<String, String> data) {
}
