package com.memento.server.api.service.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.api.service.notification.dto.response.NotificationUnreadResponse;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public NotificationListResponse getNotifications(NotificationListQueryRequest request) {
		List<NotificationResponse> notifications = notificationRepository.findNotificationsByAssociateWithCursor(
			request);

		boolean hasNext = notifications.size() > request.size();
		if (hasNext) {
			notifications = notifications.subList(0, request.size());
		}

		Long nextCursor = notifications.isEmpty() ? null : notifications.get(notifications.size() - 1).id();

		return NotificationListResponse.of(notifications, PageInfo.of(hasNext, nextCursor));
	}

	public NotificationUnreadResponse getUnread(Long associateId) {
		int unreadCount = notificationRepository.countUnreadNotificationsByAssociate(associateId);
		boolean hasUnread = unreadCount > 0;

		return NotificationUnreadResponse.of(hasUnread, unreadCount);
	}
}
