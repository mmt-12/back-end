package com.memento.server.api.service.eventMessage;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.api.service.eventMessage.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventMessagePublisher {

	private final ApplicationEventPublisher publisher;

	public void publishNotification(NotificationEvent event) {
		publisher.publishEvent(event);
	}
}
