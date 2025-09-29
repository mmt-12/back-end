package com.memento.server.api.service.fcm.dto;

import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record ProfileImageMessageDto(
	String title,
	String content,
	NotificationType type,
	Long receiverId
) implements FCMMessageDto {


	public static ProfileImageMessageDto from(Notification notification) {
		return ProfileImageMessageDto.builder()
			.title(notification.getTitle())
			.content(notification.getContent())
			.type(notification.getType())
			.receiverId(notification.getReceiver().getId())
			.build();
	}
}
