package com.memento.server.api.service.eventMessage;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.api.service.eventMessage.dto.FCMEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FCMEventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publishNotification(FCMEvent event) {
		publisher.publishEvent(event);
	}
}
