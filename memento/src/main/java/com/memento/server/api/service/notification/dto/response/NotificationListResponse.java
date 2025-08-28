package com.memento.server.api.service.notification.dto.response;

import java.util.List;

import com.memento.server.common.dto.response.PageInfo;

import lombok.Builder;

@Builder
public record NotificationListResponse(
	List<NotificationResponse> notifications,
	PageInfo pageInfo
) {

	public static NotificationListResponse of(List<NotificationResponse> notifications, PageInfo pageInfo) {
		return NotificationListResponse.builder()
			.notifications(notifications)
			.pageInfo(pageInfo)
			.build();
	}
}
