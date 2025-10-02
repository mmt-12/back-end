package com.memento.server.api.service.notification.dto.response;

import lombok.Builder;

@Builder
public record NotificationUnreadResponse(
	boolean hasUnread,
	int count
) {

	public static NotificationUnreadResponse of(boolean hasUnread, int count) {
		return NotificationUnreadResponse.builder().hasUnread(hasUnread).count(count).build();
	}
}
