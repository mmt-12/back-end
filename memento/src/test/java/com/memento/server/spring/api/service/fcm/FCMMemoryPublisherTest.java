package com.memento.server.spring.api.service.fcm;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;

@ExtendWith(MockitoExtension.class)
public class FCMMemoryPublisherTest {

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private FCMEventPublisher fcmEventPublisher;

	@Test
	@DisplayName("알림 이벤트를 발행한다.")
	void publishNotification() {
		// given
		MemoryFCM memoryFCMEvent = MemoryFCM.from(1L, 1L);

		// when
		fcmEventPublisher.publishNotification(memoryFCMEvent);

		// then
		verify(applicationEventPublisher).publishEvent(memoryFCMEvent);
	}
}
