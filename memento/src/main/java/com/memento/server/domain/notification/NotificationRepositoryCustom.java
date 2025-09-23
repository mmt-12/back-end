package com.memento.server.domain.notification;

import java.util.List;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;

public interface NotificationRepositoryCustom {

	List<Notification> findNotificationsByAssociateWithCursor(NotificationListQueryRequest request);
	
	int countUnreadNotificationsByAssociate(Long associateId);
}