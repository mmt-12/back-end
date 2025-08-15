package com.memento.server.api.service.notification.dto.request;

import lombok.Builder;

@Builder
public record NotificationListQueryRequest(
	Long associateId,
	Long cursor,
	int size
) {

	public static NotificationListQueryRequest of(Long associateId, Long cursor, int size) {
		return NotificationListQueryRequest.builder()
			.associateId(associateId)
			.cursor(cursor)
			.size(size)
			.build();
	}
}
