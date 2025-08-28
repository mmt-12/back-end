package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record MemoryNotification(
	Long memoryId,
	Long authorId
) implements NotificationEvent {

	public static MemoryNotification from(Long memoryId, Long authorId) {
		return MemoryNotification.builder()
			.memoryId(memoryId)
			.authorId(authorId)
			.build();
	}
}
