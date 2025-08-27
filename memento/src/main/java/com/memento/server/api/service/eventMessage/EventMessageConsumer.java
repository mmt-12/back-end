package com.memento.server.api.service.eventMessage;

import static com.memento.server.domain.notification.NotificationType.MEMORY;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.eventMessage.dto.MemoryNotification;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventMessageConsumer {

	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;

	@Async
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryNotification event) {
		List<Notification> notificationList = new ArrayList<>();
		Memory memory = event.memory();
		Community community = memory.getEvent().getCommunity();
		List<Associate> associates = associateRepository.findAllByCommunity(community);

		for (Associate associate : associates) {
			if (associate.equals(memory.getEvent().getAssociate())) continue;
			notificationList.add(Notification.builder()
				.title(MEMORY.getTitle())
				.content(MEMORY.getTitle())
				.type(MEMORY)
				.receiver(associate)
				.memoryId(memory.getId())
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}
}
