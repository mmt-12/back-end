package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record PostNotification(
	String memoryName,
	Long memoryId,
	Long postId
) implements NotificationEvent {

	public static PostNotification from(String memoryName, Long memoryId, Long postId) {
		return PostNotification.builder()
			.memoryName(memoryName)
			.memoryId(memoryId)
			.postId(postId)
			.build();
	}
}
