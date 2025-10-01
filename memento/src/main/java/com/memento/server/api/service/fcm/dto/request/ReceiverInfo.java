package com.memento.server.api.service.fcm.dto.request;

import lombok.Builder;

@Builder
public record ReceiverInfo(
	Long id,
	String content
) {

	public static ReceiverInfo of(Long id, String content) {
		return ReceiverInfo.builder().id(id).content(content).build();
	}
}
