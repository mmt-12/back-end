package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record ReactionNotification(
	String title,
	String content,
	Long actorId,
	Long receiverId,
	Long memoryId,
	Long postId
) implements NotificationEvent {

	public static ReactionNotification of(String title, String content, Long actorId, Long receiverId, Long memoryId, Long postId) {
		return ReactionNotification.builder()
			.title(title)
			.content(content)
			.actorId(actorId)
			.receiverId(receiverId)
			.memoryId(memoryId)
			.postId(postId)
			.build();
	}
}
