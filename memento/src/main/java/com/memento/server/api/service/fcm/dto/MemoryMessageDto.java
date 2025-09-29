package com.memento.server.api.service.fcm.dto;

import java.util.List;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record MemoryMessageDto(
	String title,
	NotificationType type,
	Long memoryId,
	List<ReceiverInfo> receivers
) implements FCMMessageDto {

	public static MemoryMessageDto from(String title, NotificationType type, Long memoryId,
		List<ReceiverInfo> receivers) {
		return MemoryMessageDto.builder()
			.title(title)
			.type(type)
			.memoryId(memoryId)
			.receivers(receivers)
			.build();
	}
}
