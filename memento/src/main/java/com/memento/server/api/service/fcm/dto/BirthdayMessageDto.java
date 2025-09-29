package com.memento.server.api.service.fcm.dto;

import java.util.List;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record BirthdayMessageDto(
	String title,
	NotificationType type,
	Long associateId,
	List<ReceiverInfo> receivers
) implements FCMMessageDto {

	public static BirthdayMessageDto from(String title, NotificationType type, Long associateId,
		List<ReceiverInfo> receivers) {
		return BirthdayMessageDto.builder()
			.title(title)
			.type(type)
			.associateId(associateId)
			.receivers(receivers)
			.build();
	}
}
