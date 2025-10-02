package com.memento.server.api.service.fcm;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.api.service.fcm.dto.event.FCMEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FCMEventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publishNotification(FCMEvent event) {
		publisher.publishEvent(event);
	}
}
