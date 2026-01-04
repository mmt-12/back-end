package com.memento.server.api.service.email;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.api.service.email.dto.event.EmailEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailEventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publish(EmailEvent event) {
		publisher.publishEvent(event);
	}
}
