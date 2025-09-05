package com.memento.server.api.service.notification.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record NotificationListResponse(
	List<NotificationResponse> notifications,
	Long nextCursor,
	boolean hasNext
) {

	public static NotificationListResponse of(List<NotificationResponse> notifications, Long nextCursor, boolean hasNext) {
		return NotificationListResponse.builder()
			.notifications(notifications)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
