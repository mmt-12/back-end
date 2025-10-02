package com.memento.server.api.service.fcm.dto.event;

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
