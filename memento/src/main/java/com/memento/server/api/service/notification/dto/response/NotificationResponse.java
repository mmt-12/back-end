package com.memento.server.api.service.notification.dto.response;

import java.time.LocalDateTime;

import com.memento.server.domain.notification.Notification;

import lombok.Builder;

@Builder
public record NotificationResponse(
	Long id,
	String title,
	String content,
	Boolean isRead,
	String type,
	Long actorId,
	Long memoryId,
	Long postId,
	LocalDateTime createdAt
) {

	public static NotificationResponse from(Notification notification) {
		return NotificationResponse.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.content(notification.getContent())
			.isRead(notification.getIsRead())
			.type(notification.getType().toString())
			.actorId(notification.getActorId())
			.memoryId(notification.getMemoryId())
			.postId(notification.getPostId())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
