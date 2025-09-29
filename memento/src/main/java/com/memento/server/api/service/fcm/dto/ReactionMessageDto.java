package com.memento.server.api.service.fcm.dto;

import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record ReactionMessageDto(
	String title,
	String content,
	NotificationType type,
	Long memoryId,
	Long postId,
	Long receiverId
) implements FCMMessageDto {

	public static ReactionMessageDto from(Notification notification) {
		return ReactionMessageDto.builder()
			.title(notification.getTitle())
			.content(notification.getContent())
			.type(notification.getType())
			.memoryId(notification.getMemoryId())
			.postId(notification.getPostId())
			.receiverId(notification.getReceiver().getId())
			.build();
	}
}
