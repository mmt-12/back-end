package com.memento.server.api.service.fcm.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record FCMRequest(
	String title,
	String content,
	List<Long> receiverIds,
	FCMData dataDto
) {

	public static FCMRequest of(String title, String content, List<Long> receiverIds, FCMData dataDto) {
		return FCMRequest.builder()
			.title(title)
			.content(content)
			.receiverIds(receiverIds)
			.dataDto(dataDto)
			.build();
	}
}
