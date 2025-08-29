package com.memento.server.api.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationUnreadResponse;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public NotificationListResponse getNotifications(NotificationListQueryRequest request) {

		return null;
	}

	public NotificationUnreadResponse getUnread(Long associateId) {

		return null;
	}
}
