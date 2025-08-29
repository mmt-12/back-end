package com.memento.server.notification;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationType;

public class NotificationFixtures {

	private static final String TITLE = "알림 제목";
	private static final String CONTENT = "알림 내용";
	private static final NotificationType TYPE = NotificationType.POST;
	private static final Long ACTOR_ID = 1L;

	public static Notification notification(Associate receiver) {
		return Notification.builder()
			.title(TITLE)
			.content(CONTENT)
			.isRead(false)
			.type(TYPE)
			.actorId(ACTOR_ID)
			.receiver(receiver)
			.build();
	}

	public static Notification notification(String title, boolean isRead, Associate receiver) {
		return Notification.builder()
			.title(title)
			.content(CONTENT)
			.isRead(isRead)
			.type(TYPE)
			.actorId(ACTOR_ID)
			.receiver(receiver)
			.build();
	}

	public static Notification notification(String title, String content, boolean isRead, NotificationType type, Associate receiver) {
		return Notification.builder()
			.title(title)
			.content(content)
			.isRead(isRead)
			.type(type)
			.actorId(ACTOR_ID)
			.receiver(receiver)
			.build();
	}

	public static Notification readNotification(Associate receiver) {
		return notification(TITLE, true, receiver);
	}

	public static Notification unreadNotification(Associate receiver) {
		return notification(TITLE, false, receiver);
	}
}