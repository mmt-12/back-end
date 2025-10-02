package com.memento.server.api.service.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.api.service.notification.dto.response.NotificationUnreadResponse;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional
	public NotificationListResponse getNotifications(NotificationListQueryRequest request) {
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		boolean hasNext = notifications.size() > request.size();
		if (hasNext) {
			notifications = notifications.subList(0, request.size());
		}

		Long nextCursor = notifications.isEmpty() ? null : notifications.getLast().getId();

		List<NotificationResponse> notificationResponses = notifications.stream()
			.map(NotificationResponse::from)
			.toList();

		notifications.stream().filter(notification -> !notification.getIsRead()).forEach(Notification::markAsRead);

		return NotificationListResponse.of(notificationResponses, nextCursor, hasNext);
	}

	public NotificationUnreadResponse getUnread(Long associateId) {
		int unreadCount = notificationRepository.countUnreadNotificationsByAssociate(associateId);
		boolean hasUnread = unreadCount > 0;

		return NotificationUnreadResponse.of(hasUnread, unreadCount);
	}
}
