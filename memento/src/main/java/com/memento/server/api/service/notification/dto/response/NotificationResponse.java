package com.memento.server.api.service.notification.dto.response;

import lombok.Builder;

@Builder
public record NotificationResponse(
	String title,
	String content,
	Boolean isRead,
	String type,
	Long actorId,
	Long memoryId,
	Long postId
) {
}
