package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record PostNotification(
	Long memoryId,
	Long postId
) implements NotificationEvent {

	public static PostNotification from(Long memoryId, Long postId) {
		return PostNotification.builder()
			.memoryId(memoryId)
			.postId(postId)
			.build();
	}
}
