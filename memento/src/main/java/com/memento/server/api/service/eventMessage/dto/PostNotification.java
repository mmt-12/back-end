package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record PostNotification(
	Long memoryId,
	Long authorId,
	Long postId
) implements NotificationEvent {

	public static PostNotification from(Long memoryId, Long authorId, Long postId) {
		return PostNotification.builder()
			.memoryId(memoryId)
			.authorId(authorId)
			.postId(postId)
			.build();
	}
}
