package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record MbtiFCM(
	Long receiverId
) implements FCMEvent {

	public static MbtiFCM from(Long receiverId) {
		return MbtiFCM.builder()
			.receiverId(receiverId)
			.build();
	}
}
