package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record ReactionNotification(
	Long postId
) implements NotificationEvent {

	public static ReactionNotification from(Long postId) {
		return ReactionNotification.builder()
			.postId(postId)
			.build();
	}
}
