package com.memento.server.api.service.eventMessage;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.api.service.eventMessage.dto.MemoryNotification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventMessagePublisher {

	private final ApplicationEventPublisher publisher;

	public void publishNotification(MemoryNotification event) {
		publisher.publishEvent(event);
	}
}
