package com.memento.server.api.service.notification.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record NotificationListResponse(
	List<NotificationResponse> notifications,
	Long cursor,
	int size,
	Long nextCursor,
	boolean hasNext
) {
}
