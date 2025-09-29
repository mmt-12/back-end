package com.memento.server.api.service.fcm.dto;

import lombok.Builder;

@Builder
public record ReceiverInfo(
	Long receiverId,
	String content
) {
}
