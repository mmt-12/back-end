package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record NewImageFCM(
	Long receiverId
) implements FCMEvent {

	public static NewImageFCM from(Long receiverId) {
		return NewImageFCM.builder()
			.receiverId(receiverId)
			.build();
	}
}
