package com.memento.server.api.service.eventMessage;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_FOUND;
import static com.memento.server.domain.notification.NotificationType.ASSOCIATE;
import static com.memento.server.domain.notification.NotificationType.MEMORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.eventMessage.dto.AssociateNotification;
import com.memento.server.api.service.eventMessage.dto.MemoryNotification;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Async
@Service
@Transactional(propagation = REQUIRES_NEW)
@RequiredArgsConstructor
public class EventMessageConsumer {

	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;
	private final MemoryRepository memoryRepository;
	private final CommunityRepository communityRepository;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAssociateNotification(AssociateNotification event) {
		Community community = communityRepository.findById(event.communityId())
			.orElseThrow(() -> new MementoException(COMMUNITY_NOT_FOUND));
		List<Associate> associates = associateRepository.findAllByCommunity(community);

		List<Notification> notificationList = new ArrayList<>();
		for (Associate associate : associates) {
			if (associate.getId().equals(event.associateId()))
				continue;
			notificationList.add(Notification.builder()
				.title(ASSOCIATE.getTitle())
				.content(ASSOCIATE.getTitle())
				.type(ASSOCIATE)
				.actorId(event.associateId())
				.receiver(associate)
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryNotification event) {
		Memory memory = memoryRepository.findById(event.memoryId())
			.orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		Community community = communityRepository.findById(event.communityId())
			.orElseThrow(() -> new MementoException(COMMUNITY_NOT_FOUND));
		List<Associate> associates = associateRepository.findAllByCommunity(community);

		List<Notification> notificationList = new ArrayList<>();
		for (Associate associate : associates) {
			if (associate.getId().equals(event.authorId()))
				continue;
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
