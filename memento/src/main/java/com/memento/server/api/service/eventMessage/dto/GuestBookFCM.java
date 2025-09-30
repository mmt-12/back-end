package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record GuestBookFCM(
	Long receiverId
) implements FCMEvent {

	public static GuestBookFCM from(Long receiverId) {
		return GuestBookFCM.builder()
			.receiverId(receiverId)
			.build();
	}
}
