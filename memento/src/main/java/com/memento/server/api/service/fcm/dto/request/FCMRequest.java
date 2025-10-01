package com.memento.server.api.service.fcm.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record FCMRequest(
	String title,
	List<ReceiverInfo> receiverInfos,
	FCMData dataDto
) {

	public static FCMRequest of(String title, List<ReceiverInfo> receiverInfos, FCMData dataDto) {
		return FCMRequest.builder()
			.title(title)
			.receiverInfos(receiverInfos)
			.dataDto(dataDto)
			.build();
	}
}
